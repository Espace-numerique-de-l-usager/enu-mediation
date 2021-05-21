package ch.ge.ael.enu.mediation.business.validation;

import ch.ge.ael.enu.mediation.business.domain.GedData;
import ch.ge.ael.enu.mediation.business.domain.GedProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSize;

/**
 * Factorise la validation des donnees GED d'un document, presentes dans plusieurs messages JSON.
 */
public class GedDataValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GedDataValidator.class);

    private static final String FOURNISSEUR = "fournisseur";

    private static final String VERSION = "version";

    private static final String ID_DOCUMENT = "idDocument";

    private static final String ALGORITHME_HASH = "algorithmeHash";

    private static final String HASH = "hash";

    private static final int MAX_SIZE_ID_DOCUMENT = 200;

    private static final int MAX_SIZE_HASH = 500;

    public void validate(GedData gedData, String prefix) {
        checkEnum(gedData.getFournisseur(), GedProvider.class, prefix + FOURNISSEUR);
        LOGGER.info("A FAIRE : validation des champs ged.version et ged.algorithmeHash");
//        checkEnum(doc.getGedVersion(), GedVersion.class, prefix + VERSION);
//        checkEnum(doc.getGedVersion(), GedHashAlgorithm.class, prefix + ALGORITHME_HASH);

        checkSize(gedData.getIdDocument(), 1, MAX_SIZE_ID_DOCUMENT, prefix + ID_DOCUMENT);
        checkSize(gedData.getHash(), 1, MAX_SIZE_HASH, prefix + HASH);
    }

}
