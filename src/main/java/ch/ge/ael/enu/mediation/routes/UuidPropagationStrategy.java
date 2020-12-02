package ch.ge.ael.enu.mediation.routes;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Propage l'uuid d'une phase de StatusChange a l'autre.
 * L'uuid est obtenu durant la phase 1 de recuperation de la demarche.
 * Cette strategie propage l'uuid aux phases 2 et 3 de changement d'etat.
 */
public class UuidPropagationStrategy implements AggregationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(UuidPropagationStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Object val = newExchange.getProperty("uuid");   // en faire une statique UUID
        if (val != null) {
            LOGGER.debug("Setting prop uuid to " + val);
            oldExchange.setProperty("uuid", val);
        }
        return oldExchange;
    }

}
