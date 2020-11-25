package ch.ge.ael.enu.mediation.routes;

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
    private final JacksonDataFormat metierNewDemarcheDataFormat;

    @Resource
    private final JacksonDataFormat metierStatusChangeDataFormat;

    static final String RABBITMQ_QUEUE = "rabbitmq:demarche.exchange?queue=create";

    @Override
    public void configure() {
        restConfiguration()
                .host("https://" + formSolutionHost + ":" + formSolutionPort + "/" + formSolutionPath)
                .producerComponent("http");

        from(RABBITMQ_QUEUE)
          .choice()
            .when(header("Content-Type").isEqualTo(MediaType.NEW_DEMARCHE))
                .unmarshal(metierNewDemarcheDataFormat)
                .to("log:input")
                .setProperty("demarcheName", simple("${body.idClientDemande}", String.class))
                .setProperty("demarcheStatus", simple("${body.etat}", String.class))
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", simple("DUBOISPELERINY"))
//                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .bean(NewDemarcheToJwayMapper.class)
                .marshal().json()
                .to("log:input")
                .to("rest:post:alpha/file")
                .setHeader("name", exchangeProperty("demarcheName"))
                .to("rest:get:file/mine?queryParameters=name={name}&max=1&order=stepDate&reverse=true")
                .unmarshal(jwayFileListDataFormat)
                .setBody().simple("Nouvelle démarche dans Jway: ${body[0].uuid}")
                .to("stream:out")
            .when(header("Content-Type").isEqualTo(MediaType.STATUS_CHANGE))
                .unmarshal(metierStatusChangeDataFormat)
                .to("log:input")
                .setHeader("Content-Type", simple("application/json"))
                .setHeader("remote_user", simple("${body.idUsager}", String.class))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .marshal().json()
                .to("log:input")
            .otherwise()
                .to("stream:err");
    }

}
