package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.model.DocumentType;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import org.apache.tika.config.TikaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        checkSize(message.getContenu(), 1, 10 * 1000 + 1000, "contenu");

        /*
          en attente réponse ***REMOVED***/browse/ENU-424, sinon utiliser :
             TikaConfig.getDefaultConfig().getMediaTypeRegistry().getTypes().stream()
                .map(Objects::toString)
                .anyMatch(m -> m.equals(message.getMime()));
         */

        checkEnum(message.getTypeDocument(), DocumentType.class, "typeDocument");

        // validation de "mime" : voir https://stackoverflow.com/questions/65685623/validation-of-a-mime-type

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
