package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.NewCourrier;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class NewCourrierValidator {

    private static final String ID_PRESTATION = "idPrestation";

    private static final String ID_USAGER = "idUsager";

    private static final String LIBELLE_COURRIER = "libelleCourrier";

    private static final String DOCUMENTS = "documents";

    private static final String LIBELLE_DOCUMENT = "libelleDocument";

    private static final String ID_DOCUMENT_SI_METIER = "idDocumentSiMetier";

    private static final String MIME = "mime";

    private static final String CONTENU = "contenu";

    private static final String GED = "ged";

    private static final int MAX_NB_DOCUMENTS = 20;

    private final List<String> allowedMimeTypes;

    private final GedDataValidator gedDataValidator = new GedDataValidator();

    public NewCourrierValidator(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public void validate(NewCourrier message) {
        checkExistence(message.getIdPrestation(), ID_PRESTATION);
        checkExistence(message.getIdUsager(), ID_USAGER);
        checkExistence(message.getLibelleCourrier(), LIBELLE_COURRIER);
        checkExistence(message.getDocuments(), DOCUMENTS);
        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
        checkSize(message.getLibelleCourrier(), 1, MAX_SIZE_LIBELLE, LIBELLE_COURRIER);

        checkListNotEmpty(message.getDocuments(), DOCUMENTS);
        checkListMaxSize(message.getDocuments(), DOCUMENTS, MAX_NB_DOCUMENTS);

        IntStream.range(0, message.getDocuments().size()).forEach(i -> {
            NewDocument doc = message.getDocuments().get(i);
            String prefix = DOCUMENTS + "[" + i + "].";

            checkExistence(doc.getLibelleDocument(), prefix + LIBELLE_DOCUMENT);
            checkExistence(doc.getIdDocumentSiMetier(), prefix + ID_DOCUMENT_SI_METIER);
            checkExistence(doc.getMime(), prefix + MIME);
            checkExistence(doc.getContenu(), prefix + CONTENU);
            log.warn("A FAIRE : remettre le controle de l'exclusion mutuelle");
//            checkMutualExclusion(doc.getContenu(), prefix + CONTENU, doc.getGed() == null ? null : GED, prefix + GED);

            checkSize(doc.getLibelleDocument(), 1, MAX_SIZE_LIBELLE, prefix + LIBELLE_DOCUMENT);
            checkSize(doc.getIdDocumentSiMetier(), 1, MAX_SIZE_ID_DOCUMENT_SI_METIER, prefix + ID_DOCUMENT_SI_METIER);
            checkSize(doc.getMime(), 1, MAX_SIZE_MIME, prefix + MIME);
            checkSize(doc.getContenu(), 1, MAX_SIZE_CONTENU, prefix + CONTENU);

            if (! allowedMimeTypes.contains(doc.getMime())) {
                log.info("Erreur metier : type MIME [{}] pas pris en charge", doc.getMime());
                throw new ValidationException("La valeur \"" + doc.getMime()
                        + "\" du champ \"" + prefix + MIME + "\" n'est pas valide." +
                        " Les types MIME pris en charge sont : " + allowedMimeTypes);
            }

            if (doc.getGed() != null) {
                gedDataValidator.validate(doc.getGed(), prefix + "ged.");
            }
        });

        log.info("Validation OK");
    }

}
