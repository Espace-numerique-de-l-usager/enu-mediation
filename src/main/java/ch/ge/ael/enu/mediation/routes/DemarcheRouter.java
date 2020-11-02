package ch.ge.ael.enu.mediation.routes;

import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemarcheRouter extends RouteBuilder {

    @Value("${app.formsolution.host}")
    private String formSolutionHost;

    @Value("${app.formsolution.port}")
    private Integer formSolutionPort;

    @Value("${app.formsolution.path}")
    private String formSolutionPath;

    private final JacksonDataFormat jwayFileListDataFormat;
    private final JacksonDataFormat metierNewDemarcheDataFormat;

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .host("https://" + formSolutionHost + ":" + formSolutionPort + "/" + formSolutionPath)
                .producerComponent("http");

        from("rabbitmq:demarche.exchange?queue=create")
                .unmarshal(metierNewDemarcheDataFormat)
                .setProperty("demarcheName",simple("${body.name}",String.class))
                .setProperty("demarcheStatus",simple("${body.status}",String.class))
                .setHeader("Content-Type",simple("application/json"))
                .bean(NewDemarcheToJwayMapper.class)
                .marshal().json()
                .to("rest:post:alpha/file")
                .setHeader("name",exchangeProperty("demarcheName"))
                .to("rest:get:file/mine?queryParameters=name={name}&max=1&order=stepDate&reverse=true")
                .unmarshal(jwayFileListDataFormat)
                .setBody().simple("Nouvelle démarche dans Jway: ${body[0].uuid}")
                .to("stream:out");
    }
}
