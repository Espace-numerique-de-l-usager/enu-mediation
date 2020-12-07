package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.metier.model.DemarcheDelete;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiRouter extends RouteBuilder {

    private final CamelContext camelContext;

    static final String RABBITMQ_QUEUE = "demarche.exchange?queue=delete";

    @Override
    public void configure() throws Exception {
        restConfiguration().bindingMode(RestBindingMode.json);

        // Pour de futurs payloads arrivants en JSON (depuis Jway FormSolutions)
        onException(JsonParseException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setBody().constant("invalid json");

        rest("/demarche/")
                .delete("/{idClientDemande}/{idPrestation}/{idUsager}")
                .to("direct:demarcheDelete");

        from("direct:demarcheDelete")
                .id("rest-demarche-delete")
                .bean(DemarcheDelete.class)
                .marshal().json(JsonLibrary.Jackson)
                .to("rabbitmq:" + RABBITMQ_QUEUE)
                .setBody(constant("OK"));
    }
}
