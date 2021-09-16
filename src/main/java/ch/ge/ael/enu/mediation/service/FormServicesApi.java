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
package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.business.domain.v1_0.NewDemarche;
import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import ch.ge.ael.enu.mediation.exception.TechnicalException;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.jway.model.FileForStep;
import ch.ge.ael.enu.mediation.jway.model.FileForWorkflow;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.routes.processing.NewDemarcheToBrouillonReducer;
import ch.ge.ael.enu.mediation.service.technical.MessageLoggingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static ch.ge.ael.enu.mediation.routes.communication.Header.CONTENT_TYPE;
import static ch.ge.ael.enu.mediation.routes.communication.Header.REMOTE_USER;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static java.lang.String.format;

/**
 * API Jway Formsolutions
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FormServicesApi {

    private final ObjectMapper jackson;
    private final WebClient formServicesWebClient;
    private final MessageLoggingService messageLoggingService;
    private final NewDemarcheToBrouillonReducer brouillonReducer = new NewDemarcheToBrouillonReducer();
    private final NewDemarcheToJwayMapper newDemarcheToJwayMapper = new NewDemarcheToJwayMapper();

    /**
     * Pour Spring WebClient: erreurs 4xx
     */
    private final Function<ClientResponse, Mono<? extends Throwable>> clientErrorHandler = (response) -> {
        response.toEntity(String.class).subscribe(
                entity -> log.error("Client error {}", entity)
        );
        return Mono.error(new ValidationException("Client error " + response.statusCode()));
    };

    /**
     * Pour Spring WebClient: erreurs 5xx
     */
    private final Function<ClientResponse, Mono<? extends Throwable>> ServerErrorHandler = (response) -> {
        response.toEntity(String.class).subscribe(
                entity -> log.error("Erreur Jway Formsolutions {}", entity)
        );
        return Mono.error(new TechnicalException("Erreur Jway Formsolutions: " + response.bodyToMono(String.class)));
    };

    /**
     * API Jway Formsolutions GET /file
     */
    public File getFile(String demarcheId, String userId) {
        final String SEARCH_PATH = "/file/mine?name=%s&max=1&order=id&reverse=true";
        String path = format(SEARCH_PATH, demarcheId);

        List<File> demarches = getFileList(path,userId);

        if (demarches == null || demarches.isEmpty()) {
            // si on ne trouve pas de demarche, on cherche avec le prefixe "DRAFT"
            path = format(SEARCH_PATH, "(DRAFT)" + demarcheId);
            demarches = getFileList(path,userId);
            if (demarches == null || demarches.isEmpty()) {
                throw new ValidationException("Démarche introuvable: \"" + demarcheId + "\"");
            }
        }
        return demarches.get(0);
    }

    /**
     * Call API Formsolutions
     */
    private List<File> getFileList(String path, String userId) {
        return formServicesWebClient.get()
                .uri(path)
                .header(REMOTE_USER, userId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientErrorHandler)
                .onStatus(HttpStatus::is5xxServerError, ServerErrorHandler)
                .bodyToMono(new ParameterizedTypeReference<List<File>>(){}).block();
    }

    /**
     * API Jway Formsolutions POST /alpha/file
     */
    public File postFile(File file, String userId) {
        return postFileData("/alpha/file", file, userId);
    }

    /**
     * API Jway Formsolutions POST /alpha/file/{uid}/step
     */
    public File postFileStep(FileForStep file, String userId, UUID demarcheUuid) {
        String path = format("/alpha/file/%s/step", demarcheUuid);
        return postFileData(path, file, userId);
    }

    /**
     * API Jway Formsolutions POST /alpha/file/{uid} for workflow
     */
    public File postFileWorkflow(FileForWorkflow file, String userId, UUID demarcheUuid) {
        String path = format("/alpha/file/%s", demarcheUuid);
        return putFileData(path, file, userId);
    }

    private File postFileData(String path, Object file, String userId) {
        log.info("Jway API: POST " + path);
        File createdFile;
        try {
            createdFile = formServicesWebClient.post()
                    .uri(path)
                    .header(REMOTE_USER,userId)
                    .bodyValue(jackson.writeValueAsString(file))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientErrorHandler)
                    .onStatus(HttpStatus::is5xxServerError, ServerErrorHandler)
                    .bodyToMono(new ParameterizedTypeReference<File>(){}).block();
        } catch (JsonProcessingException e) {
            log.error("JSON marshalling error for file : " + file + " - Jackson error: " + e.getMessage());
            throw new TechnicalException("Erreur interne mediation - JSON marshalling");
        }
        return createdFile;
    }

    private File putFileData(String path, Object file, String userId) {
        log.info("Jway API: PUT " + path);
        File createdFile;
        try {
            createdFile = formServicesWebClient.put()
                    .uri(path)
                    .header(REMOTE_USER,userId)
                    .bodyValue(jackson.writeValueAsString(file))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientErrorHandler)
                    .onStatus(HttpStatus::is5xxServerError, ServerErrorHandler)
                    .bodyToMono(new ParameterizedTypeReference<File>(){}).block();
        } catch (JsonProcessingException e) {
            log.error("JSON marshalling error for file : " + file + " - Jackson error: " + e.getMessage());
            throw new TechnicalException("Erreur interne mediation - JSON marshalling");
        }
        return createdFile;
    }

}
