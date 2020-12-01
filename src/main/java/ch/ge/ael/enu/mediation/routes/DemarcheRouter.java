package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep1Mapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep2Mapper;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_COURS;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.SOUMISE;

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

    private final Predicate IS_NEW_DEMARCHE = header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_DEMARCHE);

    private final Predicate IS_STATUS_CHANGE = header("rabbitmq.Content-Type").isEqualTo(MediaType.STATUS_CHANGE);

    private final Predicate IS_NEW_DEMARCHE_SOUMISE = simple("${body.etat} == '" + SOUMISE + "'");

    private final Predicate IS_NEW_DEMARCHE_EN_COURS = simple("${body.etat} == '" + EN_COURS + "'");

    @Resource
    private final JacksonDataFormat jwayFileListDataFormat;

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
                .unmarshal().json(JsonLibrary.Jackson, StatusChange.class)
                .to("log:input")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:changementEtatDemarche-phase1", new UuidPropagationStrategy())
                .enrich("direct:changementEtatDemarche-phase2", new UuidPropagationStrategy())
                .to("direct:changementEtatDemarche-phase3");

        // changement d'etat d'une demarche, phase 1 : recuperation de son uuid
        from("direct:changementEtatDemarche-phase1").id("route-changement-etat-demarche-phase-1")
                .log("direct:changementEtatDemarche-phase1")
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
        from("direct:changementEtatDemarche-phase2").id("route-changement-etat-demarche-phase-2")
                .log("direct:changementEtatDemarche-phase2")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("corps JSON = ${body}")
                .to("rest:post:alpha/file/{uuid}/step");
                // valider ici 204

        // changement d'etat d'une demarche, phase 3 : changement du workflowStatus
        from("direct:changementEtatDemarche-phase3").id("route-changement-etat-demarche-phase-3")
                .log("direct:changementEtatDemarche-phase3")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep2Mapper.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("corps JSON = ${body}")
                .to("rest:put:alpha/file/{uuid}")
                .log("Changement d'etat OK");
                // valider ici 204
    }

}
