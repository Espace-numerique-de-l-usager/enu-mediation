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

/**
 * Verifie qu'un message de creation d'une demarche est valide.
 */
public class NewDemarcheValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDemarcheValidator.class);

    public NewDemarche validate(NewDemarche newDemarche) {
        LOGGER.info("Dans NewDemarcheValidator");

        checkExistence(newDemarche.getIdPrestation(), "idPrestation");
        checkExistence(newDemarche.getIdUsager(), "idUsager");
        checkExistence(newDemarche.getIdClientDemande(), "idClientDemande");
        checkExistence(newDemarche.getEtat(), "etat");

        checkSize(newDemarche.getIdPrestation(), 1, 50, "idPrestation");
        checkSize(newDemarche.getIdUsager(), 1, 50, "idUsager");
        checkSize(newDemarche.getIdClientDemande(), 1, 100, "idClientDemande");
        checkSize(newDemarche.getLibelleAction(), 1, 100, "libelleAction");

        checkEnum(newDemarche.getEtat(), DemarcheStatus.class, "etat");

        DemarcheStatus status = DemarcheStatus.valueOf(newDemarche.getEtat());
        if ((status == DEPOSEE || status == EN_TRAITEMENT) && newDemarche.getDateDepot() == null) {
            LOGGER.info("Erreur metier : champ [dateDepot] obligatoire quand etat = {}", status);
            throw new MissingFieldException("dateDepot",
                    "Ce champ est obligatoire quand etat = " + status);
        }
        if (status == EN_TRAITEMENT && newDemarche.getDateMiseEnTraitement() == null) {
            LOGGER.info("Erreur metier : champ [dateMiseEnTraitement] obligatoire quand etat = {}", status);
            throw new MissingFieldException("dateMiseEnTraitement",
                    "Ce champ est obligatoire quand etat = " + status);
        }
        if (status == TERMINEE) {
            LOGGER.info("Erreur metier : on ne peut pas creer de demarche a l'etat {}", TERMINEE);
            throw new ValidationException("On ne peut pas creer de demarche directement a l'etat " + TERMINEE);
        }

        new ActionValidator().validate(
                newDemarche.getLibelleAction(),
                newDemarche.getTypeAction(),
                newDemarche.getUrlAction(),
                newDemarche.getDateEcheanceAction());

        LOGGER.info("Validation OK");
        return newDemarche;  // important !
    }

}
