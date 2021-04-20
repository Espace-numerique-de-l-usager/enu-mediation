package ch.ge.ael.enu.mediation.error;

import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.routes.http.Header.RABBITMQ_CONTENT_TYPE;

public class UnroutableMessageProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnroutableMessageProcessor.class);

    @Override
    public void process(Exchange exchange) {
        LOGGER.info("Dans {}", getClass().getSimpleName());

        String header = exchange.getMessage().getHeader(RABBITMQ_CONTENT_TYPE, String.class);
        if (StringUtils.isBlank(header)) {
            throw new ValidationException("Il manque l'en-tete HTTP \""
                    + RABBITMQ_CONTENT_TYPE + "\" dans le message");
        } else {
            throw new ValidationException("Valeur invalide \"" + header + "\" de l'en-tete HTTP \""
                    + RABBITMQ_CONTENT_TYPE + "\" dans le message");
        }
    }

}
