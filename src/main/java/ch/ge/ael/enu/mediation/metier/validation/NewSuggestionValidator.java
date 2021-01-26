package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.model.NewSuggestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeDate;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdUsager;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeUrl;

/**
 * Verifie qu'un message JSON de creation d'une suggestion de demarche est valide.
 */
public class NewSuggestionValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewSuggestionValidator.class);

    private static final String ID_PRESTATION = "idPrestation";

    private static final String ID_USAGER = "idUsager";

    private static final String LIBELLE_ACTION = "libelleAction";

    private static final String URL_ACTION = "urlAction";

    private static final String DATE_ECHEANCE_ACTION = "dateEcheanceAction";

    private static final String DESCRIPTION_ACTION = "descriptionAction";

    private static final String URL_PRESTATION = "urlPrestation";

    public NewSuggestion validate(NewSuggestion message) {
        LOGGER.info("Dans NewSuggestionValidator");

        final int MAX_SIZE_DESCRIPTION_ACTION = 150;

        checkExistence(message.getIdPrestation(), ID_PRESTATION);
        checkExistence(message.getIdUsager(), ID_USAGER);
        checkExistence(message.getLibelleAction(), LIBELLE_ACTION);
        checkExistence(message.getUrlAction(), URL_ACTION);
        checkExistence(message.getDateEcheanceAction(), DATE_ECHEANCE_ACTION);
        checkExistence(message.getDescriptionAction(), DESCRIPTION_ACTION);
        checkExistence(message.getUrlPrestation(), URL_PRESTATION);

        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSize(message.getLibelleAction(), 1, 50, LIBELLE_ACTION);
        checkSizeUrl(message.getUrlAction(), URL_ACTION);
        checkSizeDate(message.getDateEcheanceAction(), DATE_ECHEANCE_ACTION);
        checkSize(message.getDescriptionAction(), 1, MAX_SIZE_DESCRIPTION_ACTION, DESCRIPTION_ACTION);
        checkSizeUrl(message.getUrlPrestation(), URL_PRESTATION);

        checkDate(message.getDateEcheanceAction(), DATE_ECHEANCE_ACTION);

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
