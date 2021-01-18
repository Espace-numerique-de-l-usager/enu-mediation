package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.model.ActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkAbsentIfOtherAbsent;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkPresentIfOtherPresent;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkUrl;

/**
 * Factorize la validation d'une "action", presente dans plusieurs messages JSON (NewDemarche, StatusChange).
 */
public class ActionValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionValidator.class);

    void validate(
            String libelleAction,
            String urlAction,
            String typeAction,
            String dateEcheanceAction) {

        if (libelleAction == null) {
            checkAbsentIfOtherAbsent(typeAction, "typeAction", libelleAction, "libelleAction");
            checkAbsentIfOtherAbsent(urlAction, "urlAction", libelleAction, "libelleAction");
            checkAbsentIfOtherAbsent(dateEcheanceAction, "dateEcheanceAction", libelleAction, "libelleAction");
        } else {
            checkSize(libelleAction, 2, 50, "libelleAction");
            if (urlAction == null) {
                checkAbsentIfOtherAbsent(typeAction, "typeAction", urlAction, "urlAction");
                checkAbsentIfOtherAbsent(dateEcheanceAction, "dateEcheanceAction", urlAction, "urlAction");
            } else {
                checkEnum(typeAction, ActionType.class, "typeAction");
                checkPresentIfOtherPresent(dateEcheanceAction, "dateEcheanceAction", urlAction, "urlAction");
                checkUrl(urlAction, "urlAction");
                checkDate(dateEcheanceAction, "dateEcheanceAction");
            }
        }
    }

}
