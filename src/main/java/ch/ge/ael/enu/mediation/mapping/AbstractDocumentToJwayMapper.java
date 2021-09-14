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

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

@Slf4j
public class AbstractDocumentToJwayMapper {

    protected final String fileNameSanitizationRegex;

    public AbstractDocumentToJwayMapper(String fileNameSanitizationRegex) {
        this.fileNameSanitizationRegex = fileNameSanitizationRegex;
    }

    protected HttpHeaders createHeaders(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    public static class CustomByteArrayResource extends ByteArrayResource {

        private final String fileName;

        public CustomByteArrayResource(byte[] fileContent, String fileName) {
            super(fileContent);
            this.fileName = fileName;
        }

        @Override
        public String getFilename() {
            return fileName;
        }
    }

}
