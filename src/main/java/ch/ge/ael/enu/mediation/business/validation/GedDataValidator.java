/*
 * Espace numerique de l'usager - enu-mediation
 *
 * Copyright (C) 2021 Republique et canton de Geneve
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.ge.ael.enu.mediation.business.validation;

import ch.ge.ael.enu.mediation.business.domain.GedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkExistence;
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

    private static final int MAX_SIZE_FOURNISSEUR = 100;

    private static final int MAX_SIZE_VERSION = 50;

    private static final int MAX_SIZE_ID_DOCUMENT = 200;

    private static final int MAX_SIZE_ALGORITHME_HASH = 100;

    private static final int MAX_SIZE_HASH = 500;

    public void validate(GedData gedData, String prefix) {
        checkExistence(gedData.getFournisseur(), prefix + FOURNISSEUR);
        checkExistence(gedData.getVersion(), prefix + VERSION);
        checkExistence(gedData.getIdDocument(), prefix + ID_DOCUMENT);
        checkExistence(gedData.getAlgorithmeHash(), prefix + ALGORITHME_HASH);
        checkExistence(gedData.getHash(), prefix + HASH);

        checkSize(gedData.getFournisseur(), 1, MAX_SIZE_FOURNISSEUR, prefix + FOURNISSEUR);
        checkSize(gedData.getVersion(), 1, MAX_SIZE_VERSION, prefix + VERSION);
        checkSize(gedData.getIdDocument(), 1, MAX_SIZE_ID_DOCUMENT, prefix + ID_DOCUMENT);
        checkSize(gedData.getAlgorithmeHash(), 1, MAX_SIZE_ALGORITHME_HASH, prefix + ALGORITHME_HASH);
        checkSize(gedData.getHash(), 1, MAX_SIZE_HASH, prefix + HASH);
    }

}
