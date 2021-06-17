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

import ch.ge.ael.enu.mediation.business.domain.NewCourrier;
import ch.ge.ael.enu.mediation.business.domain.NewDocument;
import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static ch.ge.ael.enu.mediation.business.validation.NewDocumentValidator.MAX_SIZE_CONTENU;
import static ch.ge.ael.enu.mediation.business.validation.NewDocumentValidator.MAX_SIZE_ID_DOCUMENT_SI_METIER;
import static ch.ge.ael.enu.mediation.business.validation.NewDocumentValidator.MAX_SIZE_LIBELLE;
import static ch.ge.ael.enu.mediation.business.validation.NewDocumentValidator.MAX_SIZE_MIME;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkListMaxSize;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkListNotEmpty;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkMutualExclusion;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdDemarcheSiMetier;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdUsager;

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

            checkMutualExclusion(doc.getContenu(), prefix + CONTENU, doc.getGed() == null ? null : GED, prefix + GED);

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
