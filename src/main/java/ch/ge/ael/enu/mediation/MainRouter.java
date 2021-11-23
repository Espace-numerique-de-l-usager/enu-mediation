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
package ch.ge.ael.enu.mediation;

import ch.ge.ael.enu.business.domain.v1_0.*;
import ch.ge.ael.enu.mediation.exception.IllegalMessageException;
import ch.ge.ael.enu.mediation.exception.NotFoundException;
import ch.ge.ael.enu.mediation.exception.UnsupportedMediaTypeException;
import ch.ge.ael.enu.mediation.model.exception.ValidationException;
import ch.ge.ael.enu.mediation.service.DemarcheService;
import ch.ge.ael.enu.mediation.service.DocumentService;
import ch.ge.ael.enu.mediation.service.SuggestionService;
import ch.ge.ael.enu.mediation.service.technical.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static ch.ge.ael.enu.business.domain.v1_0.EnuMediaType.*;
import static ch.ge.ael.enu.mediation.model.Header.CONTENT_TYPE;

/**
 * Traitement des messages RabbitMQ du flux principal SI metier -> ENU.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MainRouter {

    private final ObjectMapper mapper;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private final DemarcheService demarcheService;
    private final SuggestionService suggestionService;
    private final DocumentService courrierService;
    private final ResponseHandler responseHandler;

    /**
     * Le principal point d'entree de l'application : consommation d'un message RabbitMQ du flux principal.
     */
    @RabbitListener(queues = "${app.rabbitmq.queue-in}", autoStartup = "true", ackMode = "AUTO")
    public void consume(Message message) throws JsonProcessingException {
        log.debug("=******************************=");
        log.debug("=** Message reçu de RabbitMQ **=");
        log.debug("=******************************=");
        try {
            route(message);
            log.debug("Traitement OK");
            responseHandler.handleOk(message);
        } catch (Exception e) {
            responseHandler.handleKo(e, message);
        }
    }

    private void route(Message message) throws UnsupportedMediaTypeException, ValidationException, NotFoundException {
        String contentType =  message.getMessageProperties().getContentType();
        if(contentType == null) { // Workaround temporaire pour GSDU
            contentType = message.getMessageProperties().getHeader(CONTENT_TYPE);
        }
        if(contentType == null || contentType.isEmpty()) {
            log.error("Content-Type vide ou null !");
            throw new UnsupportedMediaTypeException("L'en-tête \"" + CONTENT_TYPE + "\" manque dans le message ou est vide.");
        }
        TypeReference<?> typeReference = typeReferenceMap.get(contentType);
        if(typeReference == null) {
            log.error("Content-Type non supporté : [{}]",contentType);
            throw new UnsupportedMediaTypeException(
                    "La valeur \"" + contentType + "\" de l'en-tête " + CONTENT_TYPE + " n'est pas prise en charge");
        }
        log.debug("ContentType={}",contentType);
        Object object;
        try {
            object = mapper.readValue(message.getBody(), typeReference);
        } catch (IOException e) {
            log.warn("Erreur lors de la deserialisation en un {} : {}", typeReference.getType().getTypeName(), e.getMessage());
            throw new IllegalMessageException("Erreur lors de la deserialisation du message JSON : " + e.getMessage());
        }
        log.debug("MessageType={}", typeReference.getType().getTypeName());
        log.debug("MessageBody={}", object);

        // validation metier du message
        Set<ConstraintViolation<Object>> errors = validator.validate(object);
        if(!errors.isEmpty()) {
            // Gestion des erreurs de validation
            ArrayList<String> texts = new ArrayList<>();
            errors.forEach(error -> texts.add(error.getPropertyPath() + ": " + error.getMessage() + ". Valeur passée: (" + error.getInvalidValue() + ")" ));
            throw new ValidationException(texts.toString());
        }

        switch (contentType) {
            case BROUILLON_ABANDON:
                log.warn(BROUILLON_ABANDON + ": message non implémenté");
                break;
            case BROUILLON_DEMARCHE:
                demarcheService.handleDemarcheBrouillon((BrouillonDemarche) object);
                break;
            case COURRIER:
                courrierService.handleCourrier((Courrier) object);
                break;
            case COURRIER_BINAIRE:
                courrierService.handleCourrier((CourrierBinaire) object);
                break;
            case COURRIER_HORS_DEMARCHE:
                courrierService.handleCourrier((CourrierHorsDemarche) object);
                break;
            case COURRIER_HORS_DEMARCHE_BINAIRE:
                courrierService.handleCourrier((CourrierHorsDemarcheBinaire) object);
                break;
            case DEMARCHE_ABANDONNEE:
                log.warn(DEMARCHE_ABANDONNEE + ": message non implémenté");
                break;
            case DEMARCHE_ACTION_REQUISE:
                demarcheService.handleDemarcheActionRequise((DemarcheActionRequise) object);
                break;
            case DEMARCHE_DEPOSEE:
                demarcheService.handleDemarcheDeposee((DemarcheDeposee) object);
                break;
            case DEMARCHE_EN_TRAITEMENT:
                demarcheService.handleDemarcheEnTraitement((DemarcheEnTraitement) object);
                break;
            case DEMARCHE_TERMINEE:
                demarcheService.handleDemarcheTerminee((DemarcheTerminee) object);
                break;
            case DOCUMENT_ACCES:
                log.warn(DOCUMENT_ACCES + ": message non implémenté");
                break;
            case DOCUMENT:
                courrierService.handleDocument((DocumentUsager) object);
                break;
            case DOCUMENT_BINAIRE:
                courrierService.handleDocument((DocumentUsagerBinaire) object);
                break;
            case SEQUENCE_MESSAGES:
                log.warn(SEQUENCE_MESSAGES + ": message non implémenté");
                break;
            case SUGGESTION_ABANDON:
                log.warn(SUGGESTION_ABANDON + ": message non implémenté");
                break;
            case SUGGESTION:
                suggestionService.handleNewSuggestion((Suggestion) object);
                break;
        }
    }
}
