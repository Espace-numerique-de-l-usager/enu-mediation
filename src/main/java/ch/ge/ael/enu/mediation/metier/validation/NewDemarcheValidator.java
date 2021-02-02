package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.MissingFieldException;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.BROUILLON;
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

    private static final String ID_PRESTATION = "idPrestation";

    private static final String ID_USAGER = "idUsager";

    private static final String ID_DEMARCHE_SI_METIER = "idDemarcheSiMetier";

    private static final String ETAT = "etat";

    private static final String DATE_DEPOT = "dateDepot";

    private static final String DATE_MISE_EN_TRAITEMENT = "dateMiseEnTraitement";

    public NewDemarche validate(NewDemarche message) {
        LOGGER.info("Dans {}", getClass().getSimpleName());

        final int MAX_SIZE_ETAT = 20;

        checkExistence(message.getIdPrestation(), ID_PRESTATION);
        checkExistence(message.getIdUsager(), ID_USAGER);
        checkExistence(message.getIdDemarcheSiMetier(), ID_DEMARCHE_SI_METIER);
        checkExistence(message.getEtat(), ETAT);

        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
        checkSize(message.getEtat(), 1, MAX_SIZE_ETAT, ETAT);

        checkEnum(message.getEtat(), DemarcheStatus.class, ETAT);

        DemarcheStatus status = DemarcheStatus.valueOf(message.getEtat());
        if ((status == DEPOSEE || status == EN_TRAITEMENT) && message.getDateDepot() == null) {
            LOGGER.info("Erreur metier : champ [{}] obligatoire quand {} = {}", DATE_DEPOT, ETAT, status);
            throw new MissingFieldException(DATE_DEPOT,
                    "Ce champ est obligatoire quand " + ETAT + " = " + status);
        }
        if (status == EN_TRAITEMENT && message.getDateMiseEnTraitement() == null) {
            LOGGER.info("Erreur metier : champ [{}] obligatoire quand {} = {}", DATE_MISE_EN_TRAITEMENT, ETAT, status);
            throw new MissingFieldException(DATE_MISE_EN_TRAITEMENT,
                    "Ce champ est obligatoire quand " + ETAT + " = " + status);
        }
        if (status == TERMINEE) {
            LOGGER.info("Erreur metier : on ne peut pas creer de demarche a l'etat {}", TERMINEE);
            throw new ValidationException("On ne peut pas creer de demarche directement a l'etat " + TERMINEE);
        }

        if (status == BROUILLON) {
            ValidationUtils.checkAbsentIfOtherHasValue(message.getLibelleAction(), ActionValidator.LIBELLE_ACTION,
                    status.name(), ETAT, BROUILLON.name());
            ValidationUtils.checkPresentIfOtherHasValue(message.getUrlAction(), ActionValidator.URL_ACTION,
                    status.name(), ETAT, BROUILLON.name());
            ValidationUtils.checkAbsentIfOtherHasValue(message.getTypeAction(), ActionValidator.TYPE_ACTION,
                    status.name(), ETAT, BROUILLON.name());
        } else {
            new ActionValidator().validate(
                    message.getLibelleAction(),
                    message.getUrlAction(),
                    message.getTypeAction(),
                    message.getDateEcheanceAction());
        }

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
