package ch.ge.ael.enu.mediation.routes;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforme une requete de creation de demarche (contenant ...
 */
public class NewDemarcheToEnCoursStatusChangeStrategy implements AggregationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDemarcheToEnCoursStatusChangeStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        LOGGER.debug("oldExchange = {}", oldExchange);
        LOGGER.debug("newExchange = {}", newExchange);

        Object val = newExchange.getProperty("uuid");   // en faire une statique UUID
        if (val != null) {
            LOGGER.info("Setting prop uuid to " + val);
            oldExchange.setProperty("uuid", val);
//            LOGGER.info("Setting header uuid to " + val);
//            oldExchange.getIn().setHeader("uuid", val.toString());
        }
        return oldExchange;
    }

}
