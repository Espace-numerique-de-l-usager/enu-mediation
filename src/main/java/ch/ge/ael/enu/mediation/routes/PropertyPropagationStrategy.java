package ch.ge.ael.enu.mediation.routes;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.Arrays;
import java.util.List;

/**
 * Permet de propager une ou plusieurs proprietes d'une route a la suivante.
 * <p/>
 * Par exemple, permet de propager l'uuid d'une phase de StatusChange a l'autre.
 * L'uuid est obtenu durant la phase 1 de recuperation de la demarche.
 * Cette strategie propage l'uuid aux phases 2 et 3 de changement d'etat.
 */
public class PropertyPropagationStrategy implements AggregationStrategy {

    /**
     * Noms des proprietes a propager
     */
    private List<String> properties;

    public PropertyPropagationStrategy(String... property) {
        properties = Arrays.asList(property);
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        properties.stream()
                .forEach(prop -> {
                    Object val = newExchange.getProperty(prop);
                    if (val != null) {
                        oldExchange.setProperty(prop, val);
                    }
                });
        return oldExchange;
    }

}
