package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.model.ActionType;

import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkAbsentIfOtherAbsent;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkPresentIfOtherPresent;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeUrl;

/**
 * Factorise la validation d'une "action", presente dans plusieurs messages JSON (NewDemarche, StatusChange).
 */
public class ActionValidator {

    static final String TYPE_ACTION = "typeAction";

    static final String URL_ACTION = "urlAction";

    static final String LIBELLE_ACTION = "libelleAction";

    static final String DATE_ECHEANCE_ACTION = "dateEcheanceAction";

    void validate(
            String libelleAction,
            String urlAction,
            String typeAction,
            String dateEcheanceAction) {

        if (libelleAction == null) {
            checkAbsentIfOtherAbsent(urlAction, URL_ACTION, libelleAction, LIBELLE_ACTION);
            checkAbsentIfOtherAbsent(dateEcheanceAction, DATE_ECHEANCE_ACTION, libelleAction, LIBELLE_ACTION);
        } else {
            final int MAX_SIZE_LIBELLE_ACTION = 250;
            checkSize(libelleAction, 1, MAX_SIZE_LIBELLE_ACTION, LIBELLE_ACTION);
            checkEnum(typeAction, ActionType.class, TYPE_ACTION);
            if (urlAction == null) {
                checkAbsentIfOtherAbsent(dateEcheanceAction, DATE_ECHEANCE_ACTION, urlAction, URL_ACTION);
            } else {
                checkPresentIfOtherPresent(dateEcheanceAction, DATE_ECHEANCE_ACTION, urlAction, URL_ACTION);
                checkSizeUrl(urlAction, URL_ACTION);
                checkDate(dateEcheanceAction, DATE_ECHEANCE_ACTION);
            }
        }
    }

}
