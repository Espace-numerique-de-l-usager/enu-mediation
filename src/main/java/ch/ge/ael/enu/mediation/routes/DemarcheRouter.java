package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.error.MessageFailureEnricher;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToStatusChangeMapper;
import ch.ge.ael.enu.mediation.mapping.NewDocumentToJwayMapperProcessor;
import ch.ge.ael.enu.mediation.mapping.NewSuggestionToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep1Mapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep2Mapper;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import ch.ge.ael.enu.mediation.metier.model.NewSuggestion;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import ch.ge.ael.enu.mediation.metier.validation.NewDemarcheValidator;
import ch.ge.ael.enu.mediation.metier.validation.NewDocumentValidator;
import ch.ge.ael.enu.mediation.metier.validation.StatusChangeValidator;
import ch.ge.ael.enu.mediation.util.logging.BodyReducer;
import ch.ge.ael.enu.mediation.util.logging.MultipartJwayBodyReducer;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.ge.ael.enu.mediation.mapping.NewDocumentToJwayMapperProcessor.MULTIPART_BOUNDARY;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_TRAITEMENT;

/**
 * Ceci est la classe principale de toute l'application.
 * Elle definit la consommation (et parfois la production) des messages RabbitMQ et
 * leur transmission vers FormServices.
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

    /**
     * Taille (en bytes Base 64) des fichiers au-dela de laquelle le contenu des fichiers n'est plus trace
     * dans la console et dans les fichiers de traces.
     */
    @Value("${app.mediation.logging.max-file-content-size}")
    private int maxFileContentSize;

    /**
     * Types MIME (par ex. 'applicatiopn/pdf') de documents acceptes par la mediation.
     * Si un type est ajoute a cette liste, le document n'est pas pour autant forcement accepte par FormServices,
     * qui a sa propre liste de types acceptes.
     */
    @Value("${app.mediation.document.mime-types}")
    private List<String> allowedMimeTypes;

    static final String MAIN_QUEUE = "rabbitmq:" +
            "simetier1-to-enu-main?" +
            "queue=simetier1-to-enu-main-q" +
//            "&exchangePattern=InOnly" +
            "&deadLetterExchange=enu-to-simetier1-reply" +
            "&deadLetterQueue=enu-to-simetier1-reply-q" +
            "&deadLetterRoutingKey=enu-to-simetier1-reply-q" +
            "&autoDelete=false" +
            "&autoAck=false";

    static final String REPLY_QUEUE = "rabbitmq:" +
            "enu-to-simetier1-reply?" +
            "queue=enu-to-simetier1-reply-q" +
//            "&deadLetterExchange=enu-internal-error" +
//            "&deadLetterQueue=enu-internal-error-q" +
//            "&deadLetterRoutingKey=enu-internal-error-q" +
            "&autoDelete=false";
//    static final String REPLY_QUEUE = "siclient2-to-enu?queue=siclient2-to-enu-reply&autoDelete=false&requestTimeout=5000";
//    static final String REPLY_QUEUE = "siclient2-to-enu?queue=siclient2-to-enu-reply&autoDelete=false&autoAck=false";

    static final String INTERNAL_ERROR_QUEUE = "siclient2-to-enu?queue=siclient2-to-enu-internal-error";

    private final Predicate isNewDemarche = header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_DEMARCHE);

    private final Predicate isNewSuggestion = header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_SUGGESTION);

    @Deprecated
    private final Predicate isStatusChange = header("rabbitmq.Content-Type").isEqualTo(MediaType.STATUS_CHANGE);

    private final Predicate isNewDocument = header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_DOCUMENT);

    private final Predicate isNewDemarcheDeposee = jsonpath("$[?(@.etat=='" + DEPOSEE + "')]");

    private final Predicate isNewDemarcheEnTraitement = jsonpath("$[?(@.etat=='" + EN_TRAITEMENT + "')]");

    private static final String UUID = "uuid";

    private static final String CSRF_TOKEN = "csrf-token";

    private static final String CODE_REPONSE = "Reponse : HTTP ${header.CamelHttpResponseCode}";

    /**
     * Definition des routes.
     */
    @Override
    public void configure() {
        camelContext.setStreamCaching(true);
        restConfiguration()
                .host("https://" + formSolutionHost + ":" + formSolutionPort + "/" + formSolutionPath)
                // TEMP !
//                .host("http://lab-rh712tomc143a:8080/aelportalenu/formservices/rest")
                .producerComponent("http");

        // attrape-tout
//        errorHandler(deadLetterChannel("rabbitmq:" + INTERNAL_ERROR_QUEUE).useOriginalMessage());

        onException(ValidationException.class)
//                .handled(true)
                .useOriginalMessage()
//                .useOriginalBody()
                .log("headers dans onException (avant MessageFailureEnricher) : ${headers}")
                .process(new MessageFailureEnricher())
                .log("exchangeId dans onException : ${exchangeId}")
                .log("body dans onException : ${body}")
                .log("headers dans onException : ${headers}")
                .log("Envoi a RabbitMQ du message d'erreur")
                .to(REPLY_QUEUE)
                .continued(false);

        // routage principal
        from(MAIN_QUEUE).id("route-principale")
                .log("********************************")
                .log("*** Message recu de RabbitMQ ***")
                .enrich("direct:log-message", new OldExchangeStrategy())
                .choice()
                    .when(isNewDemarche)
                        .to("direct:nouvelle-demarche")
                    .when(isNewSuggestion)
                        .to("direct:nouvelleSuggestion")
                    .when(isStatusChange)
                        .to("direct:changement-etat-demarche")
                    .when(isNewDocument)
                        .to("direct:nouveau-document")
                    .otherwise()
                        .to("stream:err");

        // trace du message entrant
        from("direct:log-message").id("log-message")
                .bean(new BodyReducer(maxFileContentSize))
                .to("log:INFO?showHeaders=true");

        // nouvelle demarche (en "brouillon", ou directement a "deposee" ou a "en traitement")
        from("direct:nouvelle-demarche").id("nouvelle-demarche")
                // prevoir un ExceptionHandler pour com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
                .log("* ROUTE nouvelle-demarche")
                .enrich("direct:nouvelle-demarche-brouillon", new OldExchangeStrategy())
                .setProperty("newDemarche", body())
                .choice()
                    .when(isNewDemarcheDeposee).id("nouvelle-demarche-deposee")
                        .log("Passage a l'etat SOUMISE")
                        .log("Body : ${body}")
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(DEPOSEE))
                        .marshal(pojoToJson())
                        .to("direct:changement-etat-demarche")
                    .when(isNewDemarcheEnTraitement).id("nouvelle-demarche-en-traitement")
                        .log("Passage a l'etat SOUMISE (avant le passage a l'etat EN_TRAITEMENT)")
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(DEPOSEE))
                        .marshal(pojoToJson())
                        .to("direct:changement-etat-demarche")
                        .log("Passage a l'etat EN_TRAITEMENT")
                        .setBody(exchangeProperty("newDemarche"))
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(EN_TRAITEMENT))
                        .marshal(pojoToJson())
                        .to("direct:changement-etat-demarche")
                    .otherwise()
                        .log("On en reste a l'etat BROUILLON");

        // nouvelle demarche (creation a l'etat brouillon)
        from("direct:nouvelle-demarche-brouillon").id("nouvelle-demarche-brouillon")
                .log("* ROUTE nouvelle-demarche-brouillon")
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
                .log(CODE_REPONSE)
                .unmarshal(jsonToPojo(File.class))
                .log("Demarche creee, uuid = ${body.uuid}");

        // nouvelle suggestion de demarche
        from("direct:nouvelleSuggestion").id("nouvelle-suggestion")
                .log("* ROUTE nouvelleSuggestion")
                .unmarshal(jsonToPojo(NewSuggestion.class))
                .to("log:input")
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .bean(NewSuggestionToJwayMapper.class)
                .marshal(pojoToJson())
                .log("JSON envoye a Jway = ${body}")
                .to("rest:post:alpha/file")
                .log(CODE_REPONSE)
                .unmarshal(jsonToPojo(File.class))
                .log("Suggestion creee, uuid = ${body.uuid}");

        // changement d'etat d'une demarche
        from("direct:changement-etat-demarche").id("changement-etat-demarche")
                .log("* ROUTE changement-etat-demarche")
                .unmarshal(jsonToPojo(StatusChange.class))
                .bean(StatusChangeValidator.class)
                .to("log:input")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:changement-etat-demarche-phase-1", new PropertyPropagationStrategy(UUID))
                .enrich("direct:changement-etat-demarche-phase-2", new PropertyPropagationStrategy(UUID))
                .to("direct:changement-etat-demarche-phase-3");

        // changement d'etat d'une demarche, phase 1 : recuperation de son uuid
        from("direct:changement-etat-demarche-phase-1").id("changement-etat-demarche-phase-1")
                .log("* ROUTE changement-etat-demarche-phase-1")
                .to("log:input")
                .setProperty("idDemarcheSiMetier", simple("${body.idDemarcheSiMetier}", String.class))
                .setHeader("name", exchangeProperty("idDemarcheSiMetier"))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .to("rest:get:file/mine?name={name}&max=1&order=id&reverse=true")  // ajouter &application.id={idPrestation}
                .log(CODE_REPONSE)
                .log("JSON obtenu de Jway = ${body}")
                .unmarshal(jsonToList(File.class))   // en faire une propriété
                .setProperty(UUID, simple("${body[0].uuid}", String.class))
                .log("uuid = ${body[0].uuid}");

        // changement d'etat d'une demarche, phase 2 : changement du step
        from("direct:changement-etat-demarche-phase-2").id("changement-etat-demarche-phase-2")
                .log("* ROUTE changement-etat-demarche-phase-2")
                .to("log:input")
                .setHeader("uuid", exchangeProperty(UUID))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .marshal(pojoToJson())
                .log("JSON envoye a Jway = ${body}")
                .to("rest:post:alpha/file/{uuid}/step")
                .log(CODE_REPONSE);
                // valider ici 204

        // changement d'etat d'une demarche, phase 3 : changement du workflowStatus
        from("direct:changement-etat-demarche-phase-3").id("changement-etat-demarche-phase-3")
                .log("* ROUTE changement-etat-demarche-phase-3")
                .to("log:input")
                .setHeader(UUID, exchangeProperty(UUID))
                .bean(StatusChangeToJwayStep2Mapper.class)
                .marshal(pojoToJson())
                .log("JSON envoye a Jway = ${body}")
                .to("rest:put:alpha/file/{uuid}")
                .log("Changement d'etat OK")
                .log(CODE_REPONSE);
                // valider ici 204

        // ajout d'un document a une demarche
        from("direct:nouveau-document").id("nouveau-document")
                .log("* ROUTE nouveau-document")
                .unmarshal(jsonToPojo(NewDocument.class))
                .bean(new NewDocumentValidator(allowedMimeTypes))
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:nouveau-document-phase-1", new PropertyPropagationStrategy(UUID))
                .enrich("direct:nouveau-document-phase-2", new PropertyPropagationStrategy(UUID, CSRF_TOKEN))
                .to("direct:nouveau-document-phase-3");

        // ajout d'un document a une demarche, phase 1 : recuperation de son uuid
        from("direct:nouveau-document-phase-1").id("nouveau-document-phase-1")
                .log("* ROUTE nouveau-document-phase-1")
                .setProperty("idDemarcheSiMetier", simple("${body.idDemarcheSiMetier}", String.class))
                .setHeader("name", exchangeProperty("idDemarcheSiMetier"))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .to("rest:get:file/mine?name={name}&max=1&order=id&reverse=true")  // ajouter &application.id={idPrestation}
                .log(CODE_REPONSE)
                .log("JSON obtenu de Jway = ${body}")
                .unmarshal(jsonToList(File.class))   // en faire une propriété
                .setProperty(UUID, simple("${body[0].uuid}", String.class))
                .log("uuid = ${body[0].uuid}");

        // ajout d'un document a une demarche, phase 2 : requete HEAD pour recuperer un jeton CSRF.
        // Sans cette phase, on obtient une erreur 403 dans la phase suivante
        from("direct:nouveau-document-phase-2").id("nouveau-document-phase-2")
                .log("* ROUTE nouveau-document-phase-2")
                .setHeader("X-CSRF-Token", simple("fetch"))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .setHeader(UUID, exchangeProperty(UUID))
                .marshal(pojoToJson())
                .log("Requete HEAD envoyee a Jway")
                .to("rest:head:document/ds/{uuid}/attachment")
                .log(CODE_REPONSE)
                .log("Jeton CSRF obtenu = ${headers.X-CSRF-Token}")
                .setProperty(CSRF_TOKEN, simple("${headers.X-CSRF-Token}", String.class));

        // ajout d'un document a une demarche, phase 3 : requete proprement dite d'envoi à Jway
        from("direct:nouveau-document-phase-3").id("nouveau-document-phase-3")
                .log("* ROUTE nouveau-document-phase-3")
                .setHeader("X-CSRF-Token", exchangeProperty(CSRF_TOKEN))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .setHeader(UUID, exchangeProperty(UUID))
                .setHeader(Exchange.CONTENT_TYPE, simple("multipart/form-data;boundary=" + MULTIPART_BOUNDARY))
//                .bean(NewDocumentToJwayMapper.class)
                .process(new NewDocumentToJwayMapperProcessor())
                .log("Headers envoyes a Jway = ${headers}")
                .enrich("direct:log-multipart-message", new OldExchangeStrategy())
                .to("rest:post:document/ds/{uuid}/attachment")
                .log(CODE_REPONSE);

        // trace du message de creation de document envoye a Jway
        from("direct:log-multipart-message").id("log-multipart-message")
                .bean(new MultipartJwayBodyReducer(maxFileContentSize))
                .log("JSON envoye a Jway = ${body}")
                .to("log:INFO?showHeaders=true");
    }

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

}
