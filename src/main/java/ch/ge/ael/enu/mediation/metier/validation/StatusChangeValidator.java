package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkUrl;

/**
 * Verifie qu'un message de changement d'etat d'une demarche est valide.
 */
public class StatusChangeValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusChangeValidator.class);

    public StatusChange validate(StatusChange statusChange) {
        LOGGER.info("Dans StatusChangeValidator");

        checkExistence(statusChange.getIdPrestation(), "idPrestation");
        checkExistence(statusChange.getIdUsager(), "idUsager");
        checkExistence(statusChange.getIdDemarcheSiMetier(), "idDemarcheSiMetier");
        checkExistence(statusChange.getNouvelEtat(), "nouvelEtat");
        checkExistence(statusChange.getDateNouvelEtat(), "dateNouvelEtat");

        checkSize(statusChange.getIdPrestation(), 1, 50, "idPrestation");
        checkSize(statusChange.getIdUsager(), 1, 50, "idUsager");
        checkSize(statusChange.getIdDemarcheSiMetier(), 1, 100, "idDemarcheSiMetier");

        checkEnum(statusChange.getNouvelEtat(), DemarcheStatus.class, "nouvelEtat");

        checkDate(statusChange.getDateEcheanceAction(), "dateEcheanceAction");

        checkUrl(statusChange.getUrlAction(), "urlAction");
        checkUrl(statusChange.getUrlRenouvellementDemarche(), "urlRenouvellementDemarche");

        new ActionValidator().validate(
                statusChange.getLibelleAction(),
                statusChange.getTypeAction(),
                statusChange.getUrlAction(),
                statusChange.getDateEcheanceAction());

        LOGGER.info("Validation OK");
        return statusChange;  // important !
    }

}
