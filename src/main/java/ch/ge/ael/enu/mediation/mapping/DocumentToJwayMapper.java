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
package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.business.domain.v1_0.DocumentUsager;
import ch.ge.ael.enu.business.domain.v1_0.DocumentUsagerBinaire;
import ch.ge.ael.enu.business.domain.v1_0.DocumentType;
import ch.ge.ael.enu.mediation.jway.model.JwayDocumentType;
import ch.ge.ael.enu.mediation.util.file.FileNameSanitizer;
import ch.ge.ael.enu.mediation.util.mime.MimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;

import java.util.Base64;

/**
 * Cree le body de la requete <strong>multipart</strong> pour Jway.
 * Cas d'usage : ajout d'un document a une demarche.
 */
@Slf4j
public class DocumentToJwayMapper extends AbstractDocumentToJwayMapper {

    public DocumentToJwayMapper(String fileNameSanitizationRegex) {
        super(fileNameSanitizationRegex);
    }

    public MultiValueMap<String, HttpEntity<?>> map(DocumentUsager newDocument) {
        // preparation des donnees : name
        String name = newDocument.getLibelleDocument()
                + "|" + newDocument.getIdDocumentSiMetier()
                + "|" + newDocument.getGed().getFournisseur()
                + "|" + newDocument.getGed().getIdDocument()
                + "|" + newDocument.getGed().getAlgorithmeHash()
                + "|" + newDocument.getGed().getHash();
        JwayDocumentType type = newDocument.typeDocument.equals(DocumentType.JUSTIFICATIF) ?
                JwayDocumentType.ATTACHMENT : JwayDocumentType.REPORT;

        // preparation des donnees : fileName
        String fileName = newDocument.getLibelleDocument() + "." + MimeUtils.getFileExtension(newDocument.getMime());
        fileName = "\"" + new FileNameSanitizer(fileNameSanitizationRegex).sanitize(fileName) + "\"";

        // preparation des donnees : type
        log.info("fileName apres assainissement = [{}]", fileName);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", name, MediaType.TEXT_PLAIN);
        builder.part("type", type, MediaType.TEXT_PLAIN);
        return builder.build();
    }

    public MultiValueMap<String, HttpEntity<?>> map(DocumentUsagerBinaire newDocument) {
        // preparation des donnees : bytes du contenu
        byte[] decodedContentAsBytes = Base64.getDecoder().decode(newDocument.getContenu());

        // preparation des donnees : name
        String name = newDocument.getLibelleDocument()
                + "|" + newDocument.getIdDocumentSiMetier();
        JwayDocumentType type = newDocument.typeDocument.equals(DocumentType.JUSTIFICATIF) ?
                JwayDocumentType.ATTACHMENT : JwayDocumentType.REPORT;

        // preparation des donnees : fileName
        String fileName = newDocument.getLibelleDocument() + "." + MimeUtils.getFileExtension(newDocument.getMime());
        fileName = "\"" + new FileNameSanitizer(fileNameSanitizationRegex).sanitize(fileName) + "\"";

        // preparation des donnees : type
        log.info("fileName apres assainissement = [{}]", fileName);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", name, MediaType.TEXT_PLAIN);
        builder.part("type", type, MediaType.TEXT_PLAIN);

        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(MediaType.TEXT_PLAIN);
        ByteArrayResource byteArrayResource = new CourrierDocumentToJwayMapper.CustomByteArrayResource(decodedContentAsBytes, fileName);
        HttpEntity<ByteArrayResource> partEntity = new HttpEntity<>(byteArrayResource, partHeaders);
        builder.part("files", partEntity, MediaType.TEXT_PLAIN);

        return builder.build();
    }
}
