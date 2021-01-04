package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.model.TypeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkAbsentIfOtherAbsent;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkPresentIfOtherPresent;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkUrl;

/**
 * Factorizes the validation of NewDemarche et StatusChange.
 */
public class ActionValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionValidator.class);

    void validate(
            String libelleAction,
            String typeAction,
            String urlAction,
            String dateEcheanceAction) {

        if (libelleAction == null) {
            checkAbsentIfOtherAbsent(typeAction, "typeAction", libelleAction, "libelleAction");
            checkAbsentIfOtherAbsent(urlAction, "urlAction", libelleAction, "libelleAction");
            checkAbsentIfOtherAbsent(dateEcheanceAction, "dateEcheanceAction", libelleAction, "libelleAction");
        } else {
            checkSize(libelleAction, 2, 50, "libelleAction");
            if (typeAction == null) {
                checkAbsentIfOtherAbsent(urlAction, "urlAction", typeAction, "typeAction");
                checkAbsentIfOtherAbsent(dateEcheanceAction, "dateEcheanceAction", typeAction, "typeAction");
            } else {
                checkEnum(typeAction, TypeAction.class, "typeAction");
                checkPresentIfOtherPresent(urlAction, "urlAction", typeAction, "typeAction");
                checkPresentIfOtherPresent(dateEcheanceAction, "dateEcheanceAction", typeAction, "typeAction");
                checkUrl(urlAction, "urlAction");
                checkDate(dateEcheanceAction, "dateEcheanceAction");
            }
        }
    }

}
