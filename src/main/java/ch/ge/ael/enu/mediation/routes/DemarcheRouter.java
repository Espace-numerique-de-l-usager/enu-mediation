package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep1Mapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep2Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
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

    @Resource
    private final JacksonDataFormat jwayFileListDataFormat;

    @Resource
    private final JacksonDataFormat jwayFileDataFormat;

    @Resource
    private final JacksonDataFormat metierNewDemarcheDataFormat;

    @Resource
    private final JacksonDataFormat metierStatusChangeDataFormat;

    static final String RABBITMQ_QUEUE = "rabbitmq:demarche.exchange?queue=create";

    @Override
    public void configure() {
        restConfiguration()
                .host("https://" + formSolutionHost + ":" + formSolutionPort + "/" + formSolutionPath)
                .producerComponent("http");

        // routage principal
        from(RABBITMQ_QUEUE)
                .log("Message recu de RabbitMQ")
                .to("log:INFO?showHeaders=true")
                .log("En-tête " + header("Content-Type"))
                .choice()
                    .when(header("rabbitmq.Content-Type").isEqualTo(MediaType.NEW_DEMARCHE))
                        .to("direct:nouvelleDemarche")
                    .when(header("rabbitmq.Content-Type").isEqualTo(MediaType.STATUS_CHANGE))
                        .to("direct:changementEtatDemarche")
                    .otherwise()
                        .to("stream:err");

        // nouvelle demarche
        from("direct:nouvelleDemarche")
                .unmarshal(metierNewDemarcheDataFormat)
                .to("log:input")
                .setProperty("demarcheStatus", simple("${body.etat}", String.class))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .bean(NewDemarcheToJwayMapper.class)
                .marshal().json()
                .log("corps JSON = ${body}")
                .to("rest:post:alpha/file")
                .unmarshal(jwayFileDataFormat)
                .setBody().simple("Nouvelle démarche dans Jway: ${body.uuid}")
                .to("stream:out");

        // changement d'etat d'une demarche
        from("direct:changementEtatDemarche")
                .log("direct:changementEtatDemarche")
                .unmarshal(metierStatusChangeDataFormat)
                .to("log:input")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:changementEtatDemarche-Recherche", new UuidPropagationStrategy())
                .enrich("direct:changementEtatDemarche-Step", new UuidPropagationStrategy())
                .to("direct:changementEtatDemarche-Workflow");

        // changement d'etat d'une demarche : recuperation de son uuid
        from("direct:changementEtatDemarche-Recherche")
                .log("direct:changementEtatDemarche-Recherche")
                .setProperty("idClientDemande", simple("${body.idClientDemande}", String.class))
                .setHeader("name", exchangeProperty("idClientDemande"))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .marshal().json()
                .to("rest:get:file/mine?queryParameters=name={name}&max=1&order=stepDate&reverse=true")
                .unmarshal(jwayFileListDataFormat)
                .setProperty("uuid", simple("${body[0].uuid}", String.class))
                .log("uuid = ${body[0].uuid}");

        // changement d'etat d'une demarche : changement d'etape, partie 1 (step)
        from("direct:changementEtatDemarche-Step")
                .log("direct:changementEtatDemarche-Step")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .marshal().json()
                .log("corps JSON = ${body}")
                .to("rest:post:alpha/file/{uuid}/step")
                .log("Appel REST pour step OK");
                // valider ici 204

        // changement d'etat d'une demarche : changement d'etape, partie 2 (workflowStatus)
        from("direct:changementEtatDemarche-Workflow")
                .log("direct:changementEtatDemarche-Workflow")
                .setHeader("uuid", exchangeProperty("uuid"))
                .bean(StatusChangeToJwayStep2Mapper.class)
                .marshal().json()
                .log("corps JSON = ${body}")
                .to("rest:put:alpha/file/{uuid}")
                .log("Appel REST pour workflow OK");
                // valider ici 204
    }

}
