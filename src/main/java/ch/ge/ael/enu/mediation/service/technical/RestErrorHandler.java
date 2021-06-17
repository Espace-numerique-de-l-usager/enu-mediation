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
package ch.ge.ael.enu.mediation.service.technical;

import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import ch.ge.ael.enu.mediation.exception.TechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

/**
 * Gestionnaire d'erreur survenue lors d'un appel a un service REST d'enu-backend.
 */
@Slf4j
public class RestErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException{
        return (response.getStatusCode().series() == CLIENT_ERROR
             || response.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == SERVER_ERROR || response.getStatusCode() == NOT_FOUND) {
            throw new TechnicalException(getErrorDescription(response, "technique"));
        } else if (response.getStatusCode().series() == CLIENT_ERROR) {
            // erreur de validation dans Jway : on prefere la propager au client
            throw new ValidationException(getErrorDescription(response, "metier"));
        }
    }

    private String getErrorDescription(ClientHttpResponse response, String type) throws IOException {
        return "Erreur " + type + " survenue lors de l'appel a Jway : "
                + response.getStatusCode().value() + " "
                + getResponseBody(response);
    }

    private String getResponseBody(ClientHttpResponse response) {
        byte[] body = new byte[0];
        try {
            body = FileCopyUtils.copyToByteArray(response.getBody());
        }
        catch (IOException e) {
            log.error("Erreur inattendue lors du traitement d'erreur", e);
        }
        return new String(body);
    }

}
