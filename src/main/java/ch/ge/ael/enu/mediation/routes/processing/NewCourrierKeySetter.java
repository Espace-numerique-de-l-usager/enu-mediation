package ch.ge.ael.enu.mediation.routes.processing;

import ch.ge.ael.enu.mediation.metier.model.NewCourrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

/**
 * Ajoute au courrier une clef technique.
 * Cette clef sera affectee a chaque document constituant le courrier, et permettra donc de regrouper
 * les documents.
 */
public class NewCourrierKeySetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewCourrierKeySetter.class);

    public NewCourrier addKey(NewCourrier newCourrier) {
        LOGGER.info("Dans {}", getClass().getSimpleName());

        String key = "Courrier-" + ZonedDateTime.now().toEpochSecond();
        newCourrier.setClef(key);

        return newCourrier;
    }

}
