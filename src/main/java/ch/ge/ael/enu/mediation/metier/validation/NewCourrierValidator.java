package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.NewCourrier;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;

import static ch.ge.ael.enu.mediation.metier.validation.NewDocumentValidator.MAX_SIZE_CONTENU;
import static ch.ge.ael.enu.mediation.metier.validation.NewDocumentValidator.MAX_SIZE_ID_DOCUMENT_SI_METIER;
import static ch.ge.ael.enu.mediation.metier.validation.NewDocumentValidator.MAX_SIZE_LIBELLE;
import static ch.ge.ael.enu.mediation.metier.validation.NewDocumentValidator.MAX_SIZE_MIME;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkListMaxSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkListNotEmpty;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdDemarcheSiMetier;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.metier.validation.ValidationUtils.checkSizeIdUsager;

/**
 * Verifie qu'un message JSON de creation d'un courrier est valide.
 */
public class NewCourrierValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewCourrierValidator.class);

    private final static int MAX_NB_DOCUMENTS = 20;

    private final List<String> allowedMimeTypes;

    public NewCourrierValidator(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public NewCourrier validate(NewCourrier message) {
        LOGGER.info("Dans {}", getClass().getSimpleName());

        checkExistence(message.getIdPrestation(), "idPrestation");
        checkExistence(message.getIdUsager(), "idUsager");
        checkExistence(message.getLibelleCourrier(), "libelleCourrier");
        checkExistence(message.getDocuments(), "documents");
        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
        checkSize(message.getLibelleCourrier(), 1, MAX_SIZE_LIBELLE, "libelleCourrier");

        checkListNotEmpty(message.getDocuments(), "documents");
        checkListMaxSize(message.getDocuments(), "documents", MAX_NB_DOCUMENTS);

        IntStream.range(0, message.getDocuments().size()).forEach(i -> {
            NewDocument doc = message.getDocuments().get(i);
            String prefix = "documents[" + i + "].";

            checkExistence(doc.getLibelleDocument(), prefix + "libelleDocument");
            checkExistence(doc.getIdDocumentSiMetier(), prefix + "idDocumentSiMetier");
            checkExistence(doc.getMime(), prefix + "mime");
            checkExistence(doc.getContenu(), prefix + "contenu");

            checkSize(doc.getLibelleDocument(), 1, MAX_SIZE_LIBELLE, prefix + "libelleDocument");
            checkSize(doc.getIdDocumentSiMetier(), 1, MAX_SIZE_ID_DOCUMENT_SI_METIER, prefix + "idDocumentSiMetier");
            checkSize(doc.getMime(), 1, MAX_SIZE_MIME, prefix + "mime");
            checkSize(doc.getContenu(), 1, MAX_SIZE_CONTENU, prefix + "contenu");

            if (! allowedMimeTypes.contains(doc.getMime())) {
                LOGGER.info("Erreur metier : type MIME [{}] pas pris en charge", doc.getMime());
                throw new ValidationException("La valeur \"" + doc.getMime()
                        + "\" du champ \"" + prefix + "mime\" n'est pas valide." +
                        " Les types MIME pris en charge sont : " + allowedMimeTypes);
            }
        });

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
