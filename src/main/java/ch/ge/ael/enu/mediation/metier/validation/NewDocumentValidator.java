package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.DocumentType;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdDemarcheSiMetier;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdUsager;

/**
 * Verifie qu'un message JSON d'ajout d'un document a une demarche est valide.
 */
public class NewDocumentValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDocumentValidator.class);

    private List<String> allowedMimeTypes;

    public NewDocumentValidator(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public NewDocument validate(NewDocument message) {
        LOGGER.info("Dans NewDocumentValidator");

        checkExistence(message.getIdPrestation(), "idPrestation");
        checkExistence(message.getIdUsager(), "idUsager");
        checkExistence(message.getIdDemarcheSiMetier(), "idDemarcheSiMetier");
        checkExistence(message.getTypeDocument(), "typeDocument");
        checkExistence(message.getLibelleDocument(), "libelleDocument");
        checkExistence(message.getMime(), "mime");
        checkExistence(message.getContenu(), "contenu");

        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
        checkSize(message.getLibelleDocument(), 1, 50, "libelleDocument");
        checkSize(message.getIdDocumentSiMetier(), 1, 50, "idDocumentSiMetier");
        checkSize(message.getMime(), 1, 50, "mime");
        checkSize(message.getContenu(), 1, 10 * 1000 * 1000, "contenu");

        checkEnum(message.getTypeDocument(), DocumentType.class, "typeDocument");

        if (! allowedMimeTypes.contains(message.getMime())) {
            LOGGER.info("Erreur metier : type MIME [{}] pas pris en charge", message.getMime());
            throw new ValidationException("Le type MIME \"" + message.getMime() + "\" n'est pas pris en charge");
        }

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
