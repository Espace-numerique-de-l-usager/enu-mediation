package ch.ge.ael.enu.mediation.util.logging;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Cree une trace INFO de l'exchange.
 * Abrege la trace quand elle est trop longue, par ex. quand le body contient un fichier entier.
 */
public class ExchangeLogger implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        //                 .to("log:INFO?showHeaders=true")



    }

}
