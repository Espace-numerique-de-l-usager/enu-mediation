package ch.ge.ael.enu.mediation.error;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ajoute au message en cours traitement - suppose en erreur - le contenu de l'erreur detectee.
 * Ceci permettra d'envoyer dans la queue d'erreur un message avec la raison de son rejet.
 */
public class MessageFailureEnricher implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFailureEnricher.class);

    @Override
    public void process(Exchange exchange) {
        LOGGER.info("Dans {}", getClass().getSimpleName());

        Exception e = exchange.getException();
        e = e == null ? exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class) : e;

        if (e == null) {
            LOGGER.warn("Etrange : pas d'exception trouvee dans l'exchange, alors que l'on est dans {}", getClass().getSimpleName());
        } else {
            process(exchange, e);
            LOGGER.info("OK MessageFailureEnricher");
        }
    }

    private void process(Exchange exchange, Exception e) {
        exchange.getIn().setHeader("EnuFailureMessage", e.getMessage());
        exchange.getMessage().removeHeader("rabbitmq.ROUTING_KEY");  // de https://camel.apache.org/components/latest/rabbitmq-component.html, dans "pitfall"
        exchange.getMessage().removeHeader("rabbitmq.EXCHANGE_NAME");  // de https://camel.apache.org/components/latest/rabbitmq-component.html, dans "pitfall"
//        exchange.getMessage().removeHeader("CamelRabbitmqRoutingKey");  // de https://camel.apache.org/components/latest/rabbitmq-component.html, dans "pitfall". Ne marche pas
        LOGGER.info("exchange.getIn().getHeaders : {}", exchange.getIn().getHeaders());
    }

}
