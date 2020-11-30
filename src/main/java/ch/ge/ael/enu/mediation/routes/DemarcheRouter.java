package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep1Mapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep2Mapper;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RequiredArgsConstructor
public class DemarcheRouter extends RouteBuilder {

    @Value("${app.formsolution.host}")
    private String formSolutionHost;

    @Value("${app.formsolution.port}")
    private Integer formSolutionPort;

    @Value("${app.formsolution.path}")
    private String formSolutionPath;

    static final String RABBITMQ_QUEUE = "demarche.exchange?queue=create";

    private final Predicate IS_NEW_DEMARCHE = header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_DEMARCHE);

    private final Predicate IS_STATUS_CHANGE = header("rabbitmq.Content-Type").isEqualTo(MediaType.STATUS_CHANGE);

    private final Predicate IS_NEW_DEMARCHE_SOUMISE = simple("${body.dateDepot} != null");

    private final Predicate IS_NEW_DEMARCHE_EN_COURS = simple("${body.dateMiseEnTraitement} != null");

    /*
    private final Predicate IS_NEW_DEMARCHE_SOUMISE_OU_EN_COURS =
            PredicateBuilder.or(IS_NEW_DEMARCHE_SOUMISE, IS_NEW_DEMARCHE_EN_COURS);
     */

    @Override
    public void configure() {

        restConfiguration()
                .host("https://" + formSolutionHost + ":" + formSolutionPort + "/" + formSolutionPath)
                .producerComponent("http");

        // routage principal
        from("rabbitmq:" + RABBITMQ_QUEUE).id("route-principale")
                .log("Message recu de RabbitMQ")
                .to("log:INFO?showHeaders=true")
                .log("En-tête " + header("Content-Type"))
                .choice()
                    .when(IS_NEW_DEMARCHE)
                        .to("direct:nouvelleDemarche")
                    .when(IS_STATUS_CHANGE)
                        .to("direct:changementEtatDemarche")
                    .otherwise()
                        .to("stream:err");

        // nouvelle demarche (brouillon, soumise ou en cours)
        from("direct:nouvelleDemarche").id("route-nouvelle-demarche")
                // prevoir un ExceptionHandler pour com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
                .log("direct:nouvelleDemarche")
                .enrich("direct:nouvelleDemarcheBrouillon")

                //.choice()
                  //  .when(IS_NEW_DEMARCHE_EN_COURS)
                 //       .enrich("direct:changementEtatDemarche" /* ,NewDemarcheToSoumiseStatusChangeTransformer.class */)
                //        .enrich("direct:changementEtatDemarche" /* ,NewDemarcheToEnCoursStatusChangeTransformer.class */)
//                    .when(IS_NEW_DEMARCHE_SOUMISE)
//                        .enrich("direct:changementEtatDemarche", NewDemarcheToEnCoursStatusChangeStrategy.class)
                    //.otherwise()
//                        .to("stream:out");
                    .to("stream:out");

        // nouvelle demarche (creation a l'etat brouillon)
        from("direct:nouvelleDemarcheBrouillon").id("route-nouvelle-demarche-brouillon")
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
                .setBody().simple("Nouvelle démarche dans Jway: ${body.uuid}");

        // changement d'etat d'une demarche
        from("direct:changementEtatDemarche").id("route-changement-etat-demarche")
                .log("direct:changementEtatDemarche")
                .unmarshal().json(JsonLibrary.Jackson)
                .to("log:input")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:changementEtatDemarche-phase1", new UuidPropagationStrategy())
                .enrich("direct:changementEtatDemarche-phase2", new UuidPropagationStrategy())
                .to("direct:changementEtatDemarche-phase3");

        // changement d'etat d'une demarche, phase 1 : recuperation de son uuid
        from("direct:changementEtatDemarche-phase1").id("route-changement-etat-demarche-phase-1")
                .log("direct:changementEtatDemarche-Recherche")
                .setProperty("idClientDemande", simple("${body.idClientDemande}", String.class))
                .setHeader("name", exchangeProperty("idClientDemande"))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .marshal().json()
                .to("rest:get:file/mine?queryParameters=name={name}&max=1&order=stepDate&reverse=true")
                .unmarshal().json(JsonLibrary.Jackson)
                .setProperty("uuid", simple("${body[0].uuid}", String.class))
                .log("uuid = ${body[0].uuid}");

        // changement d'etat d'une demarche, phase 2 : changement du step
        from("direct:changementEtatDemarche-phase2").id("route-changement-etat-demarche-phase-2")
                .log("direct:changementEtatDemarche-Step")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("corps JSON = ${body}")
                .to("rest:post:alpha/file/{uuid}/step")
                .log("Appel REST pour step OK");
                // valider ici 204

        // changement d'etat d'une demarche, phase 3 : changement du workflowStatus
        from("direct:changementEtatDemarche-phase3").id("route-changement-etat-demarche-phase-3")
                .log("direct:changementEtatDemarche-Workflow")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep2Mapper.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("corps JSON = ${body}")
                .to("rest:put:alpha/file/{uuid}")
                .log("Appel REST pour workflow OK");
                // valider ici 204
    }

}
