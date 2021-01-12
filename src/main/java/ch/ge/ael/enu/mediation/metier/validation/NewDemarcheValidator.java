package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.MissingFieldException;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_TRAITEMENT;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.TERMINEE;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdDemarcheSiMetier;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdUsager;

/**
 * Verifie qu'un message JSON de creation d'une demarche est valide.
 */
public class NewDemarcheValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDemarcheValidator.class);

    public NewDemarche validate(NewDemarche message) {
        LOGGER.info("Dans NewDemarcheValidator");

        checkExistence(message.getIdPrestation(), "idPrestation");
        checkExistence(message.getIdUsager(), "idUsager");
        checkExistence(message.getIdDemarcheSiMetier(), "idDemarcheSiMetier");
        checkExistence(message.getEtat(), "etat");

        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
        checkSize(message.getLibelleAction(), 1, 100, "libelleAction");

        checkEnum(message.getEtat(), DemarcheStatus.class, "etat");

        DemarcheStatus status = DemarcheStatus.valueOf(message.getEtat());
        if ((status == DEPOSEE || status == EN_TRAITEMENT) && message.getDateDepot() == null) {
            LOGGER.info("Erreur metier : champ [dateDepot] obligatoire quand etat = {}", status);
            throw new MissingFieldException("dateDepot",
                    "Ce champ est obligatoire quand etat = " + status);
        }
        if (status == EN_TRAITEMENT && message.getDateMiseEnTraitement() == null) {
            LOGGER.info("Erreur metier : champ [dateMiseEnTraitement] obligatoire quand etat = {}", status);
            throw new MissingFieldException("dateMiseEnTraitement",
                    "Ce champ est obligatoire quand etat = " + status);
        }
        if (status == TERMINEE) {
            LOGGER.info("Erreur metier : on ne peut pas creer de demarche a l'etat {}", TERMINEE);
            throw new ValidationException("On ne peut pas creer de demarche directement a l'etat " + TERMINEE);
        }

        new ActionValidator().validate(
                message.getLibelleAction(),
                message.getTypeAction(),
                message.getUrlAction(),
                message.getDateEcheanceAction());

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
