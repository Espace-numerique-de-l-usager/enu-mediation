package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToStatusChangeMapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep1Mapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep2Mapper;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_TRAITEMENT;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.DEPOSEE;

@Component
@RequiredArgsConstructor
public class DemarcheRouter extends RouteBuilder {

    @Resource
    private CamelContext camelContext;

    @Value("${app.formsolution.host}")
    private String formSolutionHost;

    @Value("${app.formsolution.port}")
    private Integer formSolutionPort;

    @Value("${app.formsolution.path}")
    private String formSolutionPath;

    static final String RABBITMQ_QUEUE = "demarche.exchange?queue=create";

    private final Predicate isNewDemarche = header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_DEMARCHE);

    private final Predicate isStatusChange = header("rabbitmq.Content-Type").isEqualTo(MediaType.STATUS_CHANGE);

    private final Predicate isNewDemarcheDeposee = jsonpath("$[?(@.etat=='" + DEPOSEE + "')]");

    private final Predicate isNewDemarcheEnTraitement = jsonpath("$[?(@.etat=='" + EN_TRAITEMENT + "')]");

    private final UuidPropagationStrategy uuidPropagationStrategy = new UuidPropagationStrategy();

    @Override
    public void configure() {
        camelContext.setStreamCaching(true);
        restConfiguration()
                .host("https://" + formSolutionHost + ":" + formSolutionPort + "/" + formSolutionPath)
                .producerComponent("http");

        // routage principal
        from("rabbitmq:" + RABBITMQ_QUEUE).id("route-principale")
                .log("Message recu de RabbitMQ")
                .to("log:INFO?showHeaders=true")
                .log("En-tête " + header("Content-Type"))
                .choice()
                    .when(isNewDemarche)
                        .to("direct:nouvelleDemarche")
                    .when(isStatusChange)
                        .to("direct:changementEtatDemarche")
                    .otherwise()
                        .to("stream:err");

        // nouvelle demarche (en brouillon, ou directement à "deposee" ou a "en traitement")
        from("direct:nouvelleDemarche").id("nouvelle-demarche")
                // prevoir un ExceptionHandler pour com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
                .log("direct:nouvelleDemarche")
                .enrich("direct:nouvelleDemarcheBrouillon", new OldExchangeStrategy())
                .setProperty("newDemarche", body())
                .choice()
                    .when(isNewDemarcheDeposee)
                        .log("Passage a l'etat SOUMISE")
                        .log("Body : ${body}")
                        .unmarshal().json(JsonLibrary.Jackson, NewDemarche.class)
                        .bean(new NewDemarcheToStatusChangeMapper(DEPOSEE))
                        .marshal().json()
                        .to("direct:changementEtatDemarche")
                    .when(isNewDemarcheEnTraitement)
                        .log("Passage a l'etat SOUMISE (avant le passage a l'etat EN_TRAITEMENT)")
                        .unmarshal().json(JsonLibrary.Jackson, NewDemarche.class)
                        .bean(new NewDemarcheToStatusChangeMapper(DEPOSEE))
                        .marshal().json()
                        .to("direct:changementEtatDemarche")
                        .log("Passage a l'etat EN_TRAITEMENT")
                        .setBody(exchangeProperty("newDemarche"))
                        .unmarshal().json(JsonLibrary.Jackson, NewDemarche.class)
                        .bean(new NewDemarcheToStatusChangeMapper(EN_TRAITEMENT))
                        .marshal().json()
                        .to("direct:changementEtatDemarche")
                    .otherwise()
                        .log("On en reste a l'etat BROUILLON");

        // nouvelle demarche (creation a l'etat brouillon)
        from("direct:nouvelleDemarcheBrouillon").id("nouvelle-demarche-brouillon")
                .log("direct:nouvelleDemarcheBrouillon")
                .unmarshal().json(JsonLibrary.Jackson, NewDemarche.class)
                .to("log:input")
                .setProperty("demarcheStatus", simple("${body.etat}", String.class))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .bean(NewDemarcheToJwayMapper.class)
                .marshal().json(/*JsonLibrary.Jackson*/)
                .log("JSON envoye a Jway = ${body}")
                .to("rest:post:alpha/file")
                .unmarshal().json(JsonLibrary.Jackson, File.class)
                .log("Demarche creee, uuid = ${body.uuid}");

        // changement d'etat d'une demarche
        from("direct:changementEtatDemarche").id("changement-etat-demarche")
                .log("direct:changementEtatDemarche")
                .unmarshal().json(JsonLibrary.Jackson, StatusChange.class)
                .to("log:input")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:changementEtatDemarche-phase1", uuidPropagationStrategy)
                .enrich("direct:changementEtatDemarche-phase2", uuidPropagationStrategy)
                .to("direct:changementEtatDemarche-phase3");

        // changement d'etat d'une demarche, phase 1 : recuperation de son uuid
        from("direct:changementEtatDemarche-phase1").id("changement-etat-demarche-phase-1")
                .log("direct:changementEtatDemarche-phase1")
                .to("log:input")
                .setProperty("idClientDemande", simple("${body.idClientDemande}", String.class))
                .setHeader("name", exchangeProperty("idClientDemande"))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .to("rest:get:file/mine?queryParameters=name={name}&max=1&order=stepDate&reverse=true")  // ajouter &application.id={idPrestation}
                .log("JSON obtenu de Jway = ${body}")
                .unmarshal(new ListJacksonDataFormat(File.class))   // en faire une propriété
                .setProperty("uuid", simple("${body[0].uuid}", String.class))
                .log("uuid = ${body[0].uuid}");

        // changement d'etat d'une demarche, phase 2 : changement du step
        from("direct:changementEtatDemarche-phase2").id("changement-etat-demarche-phase-2")
                .log("direct:changementEtatDemarche-phase2")
                .to("log:input")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("JSON envoye a Jway = ${body}")
                .to("rest:post:alpha/file/{uuid}/step");
                // valider ici 204

        // changement d'etat d'une demarche, phase 3 : changement du workflowStatus
        from("direct:changementEtatDemarche-phase3").id("changement-etat-demarche-phase-3")
                .log("direct:changementEtatDemarche-phase3")
                .to("log:input")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep2Mapper.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("JSON envoye a Jway = ${body}")
                .to("rest:put:alpha/file/{uuid}")
                .log("Changement d'etat OK");
                // valider ici 204
    }

}
