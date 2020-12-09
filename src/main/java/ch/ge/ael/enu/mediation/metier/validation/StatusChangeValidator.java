package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.MissingFieldException;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import ch.ge.ael.enu.mediation.metier.model.TypeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.BROUILLON;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_TRAITEMENT;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.TERMINEE;
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
        checkExistence(statusChange.getIdClientDemande(), "idClientDemande");
        checkExistence(statusChange.getNouvelEtat(), "nouvelEtat");
        checkExistence(statusChange.getDateNouvelEtat(), "dateNouvelEtat");

        checkSize(statusChange.getIdPrestation(), 1, 50, "idPrestation");
        checkSize(statusChange.getIdUsager(), 1, 50, "idUsager");
        checkSize(statusChange.getIdClientDemande(), 1, 100, "idClientDemande");
        checkSize(statusChange.getLibelleAction(), 1, 100, "libelleAction");

        checkEnum(statusChange.getNouvelEtat(), DemarcheStatus.class, "nouvelEtat");
        checkEnum(statusChange.getTypeAction(), TypeAction.class, "typeAction");

        checkDate(statusChange.getEcheanceAction(), "echeanceAction");

        checkUrl(statusChange.getUrlAction(), "urlAction");
        checkUrl(statusChange.getUrlRenouvellementDemarche(), "urlRenouvellementDemarche");

        DemarcheStatus status = DemarcheStatus.valueOf(statusChange.getNouvelEtat());
        if (status == BROUILLON) {
            LOGGER.info("Erreur : champ [typeAction] = {}", BROUILLON);
            throw new ValidationException("Le champ \"nouvelEtat\" ne peut pas valoir \"" + BROUILLON + "\"");
        }

        if ((status == DEPOSEE || status == EN_TRAITEMENT) &&
             statusChange.getTypeAction() == null) {
                LOGGER.info("Erreur : champ [typeAction] obligatoire quand nouvelEtat = {}", status);
                throw new MissingFieldException("typeAction",
                        "Ce champ est obligatoire quand le champ \"nouvelEtat\" vaut \"" + status + "\"");
        }

        if (statusChange.getTypeAction() != null) {
            if (statusChange.getUrlAction() == null) {
                LOGGER.info("Erreur : champ [urlAction] obligatoire quand typeAction est fourni");
                throw new MissingFieldException("urlAction",
                        "Ce champ est obligatoire quand le champ \"typeAction\" est fourni");
            }
            if (statusChange.getLibelleAction() == null) {
                LOGGER.info("Erreur : champ [libelleAction] obligatoire quand typeAction est fourni");
                throw new MissingFieldException("libelleAction",
                        "Ce champ est obligatoire quand le champ \"typeAction\" est fourni");
            }
            if (statusChange.getEcheanceAction() == null) {
                LOGGER.info("Erreur : champ [echeanceAction] obligatoire quand typeAction est fourni");
                throw new MissingFieldException("echeanceAction",
                        "Ce champ est obligatoire quand le champ \"typeAction\" est fourni");
            }
        }

        if (status == TERMINEE && statusChange.getUrlRenouvellementDemarche() == null) {
            LOGGER.info("Erreur : champ [urlRenouvellementDemarche] obligatoire quand nouvelEtat = {}", TERMINEE);
            throw new MissingFieldException("urlRenouvellementDemarche",
                    "Ce champ est obligatoire quand le champ \"nouvelEtat\" vaut \"" + TERMINEE + "\"");
        }

        LOGGER.info("Validation OK");
        return statusChange;  // important !
    }

}
