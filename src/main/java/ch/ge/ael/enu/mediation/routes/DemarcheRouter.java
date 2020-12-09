package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.error.MessageFailureEnricher;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToStatusChangeMapper;
import ch.ge.ael.enu.mediation.mapping.NewSuggestionToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep1Mapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep2Mapper;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.model.NewSuggestion;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import ch.ge.ael.enu.mediation.metier.validation.NewDemarcheValidator;
import ch.ge.ael.enu.mediation.metier.validation.StatusChangeValidator;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_TRAITEMENT;

/**
 * Main RabbitMQ consumer for relaying information to FormSolutions
 */
@Component
@RequiredArgsConstructor
public class DemarcheRouter extends RouteBuilder {

    private final CamelContext camelContext;

    @Value("${app.formsolution.host}")
    private String formSolutionHost;

    @Value("${app.formsolution.port}")
    private Integer formSolutionPort;

    @Value("${app.formsolution.path}")
    private String formSolutionPath;

    static final String MAIN_QUEUE = "siclient2-to-enu?queue=siclient2-to-enu-main&autoDelete=false";

//    static final String REPLY_QUEUE = "siclient2-to-enu?queue=siclient2-to-enu-reply&routingKey=SICLI2-REPLY&autoDelete=false";
    static final String REPLY_QUEUE = "siclient3-to-enu?queue=pipo&routingKey=PIPO&autoDelete=false";

    static final String DEAD_LETTER_QUEUE = "siclient2-to-enu?queue=siclient2-to-enu-dead-letter";

    private final Predicate isNewDemarche = header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_DEMARCHE);

    private final Predicate isNewSuggestion = header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_SUGGESTION);

    private final Predicate isStatusChange = header("rabbitmq.Content-Type").isEqualTo(MediaType.STATUS_CHANGE);

    private final Predicate isNewDemarcheDeposee = jsonpath("$[?(@.etat=='" + DEPOSEE + "')]");

    private final Predicate isNewDemarcheEnTraitement = jsonpath("$[?(@.etat=='" + EN_TRAITEMENT + "')]");

    private final UuidPropagationStrategy uuidPropagationStrategy = new UuidPropagationStrategy();

    /**
     * JSON Unmarshalling to POJO using Jackson taken from the Spring context
     * @param unmarshalType Target POJO class
     * @return Camel dataformat
     */
    private DataFormatDefinition jsonToPojo(Class<?> unmarshalType) {
        JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);
        json.setUnmarshalType(unmarshalType);
        json.setAutoDiscoverObjectMapper("true");
        return json;
    }

    /**
     * JSON Unmarshalling to List using Jackson taken from the Spring context
     * @param unmarshalType Target List item POJO class
     * @return Camel dataformat
     */
    private ListJacksonDataFormat jsonToList(Class<?> unmarshalType) {
        ListJacksonDataFormat json = new ListJacksonDataFormat(unmarshalType);
        json.setUnmarshalType(unmarshalType);
        json.setAutoDiscoverObjectMapper(true);
        return json;
    }

    /**
     * JSON Marshalling using Jackson taken from the Spring context
     * @return Camel dataformat
     */
    private DataFormatDefinition pojoToJson() {
        JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);
        json.setAutoDiscoverObjectMapper("true");
        return json;
    }

    /**
     * Definition des routes.
     */
    @Override
    public void configure() {
        camelContext.setStreamCaching(true);
        restConfiguration()
                .host("https://" + formSolutionHost + ":" + formSolutionPort + "/" + formSolutionPath)
                .producerComponent("http");

        // attrape-tout
    //    errorHandler(deadLetterChannel("rabbitmq:" + DEAD_LETTER_QUEUE).useOriginalMessage());

        onException(ValidationException.class)
                .handled(true)
                .useOriginalMessage()
//                .useOriginalBody()
//                .onExceptionOccurred(new MessageFailureEnricher())
                .log("headers dans onException (avant MessageFailureEnricher) : ${headers}")
                .process(new MessageFailureEnricher())
                .log("exchangeId dans onException : ${exchangeId}")
                .log("body dans onException : ${body}")
                .log("headers dans onException : ${headers}")
                .log("Envoi a RabbitMQ du message d'erreur")
                .to("rabbitmq:" + REPLY_QUEUE);

        // routage principal
        from("rabbitmq:" + MAIN_QUEUE).id("route-principale")
                .log("*** Message recu de RabbitMQ ***")
                .to("log:INFO?showHeaders=true")
                .choice()
                    .when(isNewDemarche)
                        .to("direct:nouvelleDemarche")
                    .when(isNewSuggestion)
                        .to("direct:nouvelleSuggestion")
                    .when(isStatusChange)
                        .to("direct:changementEtatDemarche")
                    .otherwise()
                        .to("stream:err");

        // nouvelle demarche (en "brouillon", ou directement a "deposee" ou a "en traitement")
        from("direct:nouvelleDemarche").id("nouvelle-demarche")
                // prevoir un ExceptionHandler pour com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
                .log("Dans direct:nouvelleDemarche")
                .enrich("direct:nouvelleDemarcheBrouillon", new OldExchangeStrategy())
                .setProperty("newDemarche", body())
                .choice()
                    .when(isNewDemarcheDeposee)
                        .log("Passage a l'etat SOUMISE")
                        .log("Body : ${body}")
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(DEPOSEE))
                        .marshal(pojoToJson())
                        .to("direct:changementEtatDemarche")
                    .when(isNewDemarcheEnTraitement)
                        .log("Passage a l'etat SOUMISE (avant le passage a l'etat EN_TRAITEMENT)")
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(DEPOSEE))
                        .marshal(pojoToJson())
                        .to("direct:changementEtatDemarche")
                        .log("Passage a l'etat EN_TRAITEMENT")
                        .setBody(exchangeProperty("newDemarche"))
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(EN_TRAITEMENT))
                        .marshal(pojoToJson())
                        .to("direct:changementEtatDemarche")
                    .otherwise()
                        .log("On en reste a l'etat BROUILLON");

        // nouvelle demarche (creation a l'etat brouillon)
        from("direct:nouvelleDemarcheBrouillon").id("nouvelle-demarche-brouillon")
                .log("Dans direct:nouvelleDemarcheBrouillon")
                .unmarshal(jsonToPojo(NewDemarche.class))
                .bean(NewDemarcheValidator.class)
                .to("log:input")
                .setProperty("demarcheStatus", simple("${body.etat}", String.class))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .bean(NewDemarcheToJwayMapper.class)
                .marshal(pojoToJson())
                .log("JSON envoye a Jway = ${body}")
                .to("rest:post:alpha/file")
                .unmarshal(jsonToPojo(File.class))
                .log("Demarche creee, uuid = ${body.uuid}");

        // nouvelle suggestion de demarche
        from("direct:nouvelleSuggestion").id("nouvelle-suggestion")
                .log("Dans direct:nouvelleSuggestion")
                .unmarshal(jsonToPojo(NewSuggestion.class))
                .to("log:input")
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .bean(NewSuggestionToJwayMapper.class)
                .marshal(pojoToJson())
                .log("JSON envoye a Jway = ${body}")
                .to("rest:post:alpha/file")
                .unmarshal(jsonToPojo(File.class))
                .log("Suggestion creee, uuid = ${body.uuid}");

        // changement d'etat d'une demarche
        from("direct:changementEtatDemarche").id("changement-etat-demarche")
                .log("Dans direct:changementEtatDemarche")
                .unmarshal(jsonToPojo(StatusChange.class))
                .bean(StatusChangeValidator.class)
                .to("log:input")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:changementEtatDemarche-phase1", uuidPropagationStrategy)
                .enrich("direct:changementEtatDemarche-phase2", uuidPropagationStrategy)
                .to("direct:changementEtatDemarche-phase3");

        // changement d'etat d'une demarche, phase 1 : recuperation de son uuid
        from("direct:changementEtatDemarche-phase1").id("changement-etat-demarche-phase-1")
                .log("Dans direct:changementEtatDemarche-phase1")
                .to("log:input")
                .setProperty("idClientDemande", simple("${body.idClientDemande}", String.class))
                .setHeader("name", exchangeProperty("idClientDemande"))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .to("rest:get:file/mine?name={name}&max=1&order=id&reverse=true")  // ajouter &application.id={idPrestation}
                .log("JSON obtenu de Jway = ${body}")
                .unmarshal(jsonToList(File.class))   // en faire une propriété
                .setProperty("uuid", simple("${body[0].uuid}", String.class))
                .log("uuid = ${body[0].uuid}");

        // changement d'etat d'une demarche, phase 2 : changement du step
        from("direct:changementEtatDemarche-phase2").id("changement-etat-demarche-phase-2")
                .log("Dans direct:changementEtatDemarche-phase2")
                .to("log:input")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .marshal(pojoToJson())
                .log("JSON envoye a Jway = ${body}")
                .to("rest:post:alpha/file/{uuid}/step");
                // valider ici 204

        // changement d'etat d'une demarche, phase 3 : changement du workflowStatus
        from("direct:changementEtatDemarche-phase3").id("changement-etat-demarche-phase-3")
                .log("Dans direct:changementEtatDemarche-phase3")
                .to("log:input")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep2Mapper.class)
                .marshal(pojoToJson())
                .log("JSON envoye a Jway = ${body}")
                .to("rest:put:alpha/file/{uuid}")
                .log("Changement d'etat OK");
                // valider ici 204
    }

}
