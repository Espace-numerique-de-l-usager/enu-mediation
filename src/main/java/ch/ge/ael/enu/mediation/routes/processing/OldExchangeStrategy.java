package ch.ge.ael.enu.mediation.routes.processing;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rend simplement l'oldExchange.
 * Utile pour un "enrich" qui veut transmettre a l'aval son message initial, sans modifications.
 */
public class OldExchangeStrategy implements AggregationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(OldExchangeStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        LOGGER.debug("Dans OldExchangeStrategy");
        return oldExchange;
    }

}
