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

    private final List<String> allowedMimeTypes;

    public NewDocumentValidator(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public NewDocument validate(NewDocument message) {
        LOGGER.info("Dans {}", getClass().getSimpleName());

        final int MAX_SIZE_LIBELLE = 50;
        final int MAX_SIZE_ID_DOCUMENT_SI_METIER = 50;
        final int MAX_SIZE_MIME = 50;
        final int MAX_SIZE_CONTENU = 10 * 1000 * 1000;

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
        checkSize(message.getLibelleDocument(), 1, MAX_SIZE_LIBELLE, "libelleDocument");
        checkSize(message.getIdDocumentSiMetier(), 1, MAX_SIZE_ID_DOCUMENT_SI_METIER, "idDocumentSiMetier");
        checkSize(message.getMime(), 1, MAX_SIZE_MIME, "mime");
        checkSize(message.getContenu(), 1, MAX_SIZE_CONTENU, "contenu");

        checkEnum(message.getTypeDocument(), DocumentType.class, "typeDocument");

        if (! allowedMimeTypes.contains(message.getMime())) {
            LOGGER.info("Erreur metier : type MIME [{}] pas pris en charge", message.getMime());
            throw new ValidationException("Le type MIME \"" + message.getMime() + "\" n'est pas pris en charge." +
                    " Les types MIME pris en charge sont : " + allowedMimeTypes);
        }

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
