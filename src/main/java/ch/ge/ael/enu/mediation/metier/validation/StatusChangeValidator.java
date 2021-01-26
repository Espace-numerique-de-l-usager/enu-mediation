package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdDemarcheSiMetier;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdUsager;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeUrl;

/**
 * Verifie qu'un message JSON de changement d'etat d'une demarche est valide.
 */
public class StatusChangeValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusChangeValidator.class);

    private static final String ID_PRESTATION = "idPrestation";

    private static final String ID_USAGER = "idUsager";

    private static final String ID_DEMARCHE_SI_METIER = "idDemarcheSiMetier";

    private static final String NOUVEL_ETAT = "nouvelEtat";

    private static final String DATE_NOUVEL_ETAT = "dateNouvelEtat";

    private static final String URL_RENOUVELLEMENT_DEMARCHE = "urlRenouvellementDemarche";

    public StatusChange validate(StatusChange message) {
        LOGGER.info("Dans StatusChangeValidator");

        int MAX_SIZE_NOUVEL_ETAT = 20;

        checkExistence(message.getIdPrestation(), ID_PRESTATION);
        checkExistence(message.getIdUsager(), ID_USAGER);
        checkExistence(message.getIdDemarcheSiMetier(), ID_DEMARCHE_SI_METIER);
        checkExistence(message.getNouvelEtat(), NOUVEL_ETAT);
        checkExistence(message.getDateNouvelEtat(), DATE_NOUVEL_ETAT);

        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
        checkSize(message.getNouvelEtat(), 1, MAX_SIZE_NOUVEL_ETAT, NOUVEL_ETAT);
        checkSizeUrl(message.getUrlAction(), ActionValidator.URL_ACTION);
        checkSizeUrl(message.getUrlRenouvellementDemarche(), URL_RENOUVELLEMENT_DEMARCHE);

        checkEnum(message.getNouvelEtat(), DemarcheStatus.class, NOUVEL_ETAT);

        checkDate(message.getDateEcheanceAction(), ActionValidator.DATE_ECHEANCE_ACTION);

        new ActionValidator().validate(
                message.getLibelleAction(),
                message.getUrlAction(),
                message.getTypeAction(),
                message.getDateEcheanceAction());

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
