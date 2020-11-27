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
//                .setProperty("demarcheName", simple("${body.idClientDemande}", String.class))
                .setProperty("demarcheStatus", simple("${body.etat}", String.class))
                .setHeader("Content-Type", simple("application/json"))
//                .setHeader("remote_user", simple("DUBOISPELERINY"))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .bean(NewDemarcheToJwayMapper.class)
                .marshal().json()
                .to("log:input")
                .to("rest:post:alpha/file")
//                .setHeader("name", exchangeProperty("demarcheName"))
//                .to("rest:get:file/mine?queryParameters=name={name}&max=1&order=stepDate&reverse=true")
//                .unmarshal(jwayFileListDataFormat)
//                .setBody().simple("Nouvelle démarche dans Jway: ${body[0].uuid}")
                .unmarshal(jwayFileDataFormat)
                .setBody().simple("Nouvelle démarche dans Jway: ${body.uuid}")
                .to("stream:out");

        // changement d'etat d'une demarche
        from("direct:changementEtatDemarche")
                .log("direct:changementEtatDemarche")
                .unmarshal(metierStatusChangeDataFormat)
                .to("log:input")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
//                .multicast(new UuidPropagationStrategy(), false)
//                .to(
//                        "direct:changementEtatDemarche-Recherche",
//                        "direct:changementEtatDemarche-Step",
//                        "direct:changementEtatDemarche-Workflow")
//                .end();
                .enrich("direct:changementEtatDemarche-Recherche", new UuidPropagationStrategy())
                .to("direct:changementEtatDemarche-Step");

        // changement d'etat d'une demarche : recuperation de son uuid
        from("direct:changementEtatDemarche-Recherche")
                .log("direct:changementEtatDemarche-Recherche")
                .setProperty("idClientDemande", simple("${body.idClientDemande}", String.class))
                .setHeader("name", exchangeProperty("idClientDemande"))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .marshal()
                .json()
                .to("rest:get:file/mine?queryParameters=name={name}&max=1&order=stepDate&reverse=true")
                .unmarshal(jwayFileListDataFormat)
                .setProperty("uuid", simple("${body[0].uuid}", String.class))
//                .setHeader("id", exchangeProperty("uuid"));
//                .setProperty("uuid", simple("pipo1", String.class))
//                .setHeader("id", simple("pipo2", String.class))
                .log("uuid = ${body[0].uuid}");

        // changement d'etat d'une demarche : changement d'etape, partie 1 (step)
        from("direct:changementEtatDemarche-Step")
                .log("direct:changementEtatDemarche-Step")
//                .to("log:input")
                .setHeader("theId", exchangeProperty("uuid"))   // donne 400 avec unrecogized field 'id'
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .log("body 1 = ${body}")
                .marshal().json()
                .log("body 2 = ${body}")
                .to("rest:post:alpha/file/{theId}/step")
                .log("body 3 = ${body}");
                // valider ici 204

        // changement d'etat d'une demarche : changement d'etape, partie 2 (workflowStatus)
        from("direct:changementEtatDemarche-Workflow")
                .log("direct:changementEtatDemarche-Workflow")
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", exchangeProperty("remoteUser"))
                .bean(StatusChangeToJwayStep2Mapper.class)
                .to("log:input")
                .to("rest:put:alpha/file/{uuid}");
                // valider ici 204
    }

}
