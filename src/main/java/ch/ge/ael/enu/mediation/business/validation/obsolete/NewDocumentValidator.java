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
package ch.ge.ael.enu.mediation.business.validation.obsolete;

import ch.ge.ael.enu.business.domain.v1_0.NewDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Verifie qu'un message JSON d'ajout d'un document a une demarche est valide.
 */
public class NewDocumentValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDocumentValidator.class);

    static final int MAX_SIZE_LIBELLE = 50;

    static final int MAX_SIZE_ID_DOCUMENT_SI_METIER = 50;

    static final int MAX_SIZE_MIME = 50;

    static final int MAX_SIZE_CONTENU = 200 * 1024 * 1024;   // bytes en base 64

    private final List<String> allowedMimeTypes;

    public NewDocumentValidator(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public NewDocument validate(NewDocument message) {
        LOGGER.info("Dans {}", getClass().getSimpleName());
//
//        checkExistence(message.getIdPrestation(), "idPrestation");
//        checkExistence(message.getIdUsager(), "idUsager");
//        checkExistence(message.getIdDemarcheSiMetier(), "idDemarcheSiMetier");
//        checkExistence(message.getTypeDocument(), "typeDocument");
//        checkExistence(message.getLibelleDocument(), "libelleDocument");
//        checkExistence(message.getIdDocumentSiMetier(), "idDocumentSiMetier");
//        checkExistence(message.getMime(), "mime");
//        checkExistence(message.getContenu(), "contenu");
//
//        checkSizeIdPrestation(message.getIdPrestation());
//        checkSizeIdUsager(message.getIdUsager());
//        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
//        checkSize(message.getLibelleDocument(), 1, MAX_SIZE_LIBELLE, "libelleDocument");
//        checkSize(message.getIdDocumentSiMetier(), 1, MAX_SIZE_ID_DOCUMENT_SI_METIER, "idDocumentSiMetier");
//        checkSize(message.getMime(), 1, MAX_SIZE_MIME, "mime");
//        checkSize(message.getContenu(), 1, MAX_SIZE_CONTENU, "contenu");
//
//        checkEnum(message.getTypeDocument(), DocumentType.class, "typeDocument");
//
//        if (! allowedMimeTypes.contains(message.getMime())) {
//            LOGGER.info("Erreur metier : type MIME [{}] pas pris en charge", message.getMime());
//            throw new ValidationException("Le type MIME \"" + message.getMime() + "\" n'est pas pris en charge." +
//                    " Les types MIME pris en charge sont : " + allowedMimeTypes);
//        }

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
