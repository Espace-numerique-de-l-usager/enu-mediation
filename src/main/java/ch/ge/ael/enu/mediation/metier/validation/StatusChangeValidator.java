package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdDemarcheSiMetier;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdUsager;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkUrl;

/**
 * Verifie qu'un message JSON de changement d'etat d'une demarche est valide.
 */
public class StatusChangeValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusChangeValidator.class);

    public StatusChange validate(StatusChange message) {
        LOGGER.info("Dans StatusChangeValidator");

        checkExistence(message.getIdPrestation(), "idPrestation");
        checkExistence(message.getIdUsager(), "idUsager");
        checkExistence(message.getIdDemarcheSiMetier(), "idDemarcheSiMetier");
        checkExistence(message.getNouvelEtat(), "nouvelEtat");
        checkExistence(message.getDateNouvelEtat(), "dateNouvelEtat");

        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());

        checkEnum(message.getNouvelEtat(), DemarcheStatus.class, "nouvelEtat");

        checkDate(message.getDateEcheanceAction(), "dateEcheanceAction");

        checkUrl(message.getUrlAction(), "urlAction");
        checkUrl(message.getUrlRenouvellementDemarche(), "urlRenouvellementDemarche");

        new ActionValidator().validate(
                message.getLibelleAction(),
                message.getUrlAction(),
                message.getTypeAction(),
                message.getDateEcheanceAction());

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
