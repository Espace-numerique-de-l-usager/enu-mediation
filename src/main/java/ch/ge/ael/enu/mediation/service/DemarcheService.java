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

import ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus;
import ch.ge.ael.enu.business.domain.v1_0.NewDemarche;
import ch.ge.ael.enu.business.domain.v1_0.NewSuggestion;
import ch.ge.ael.enu.business.domain.v1_0.StatusChange;
import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.jway.model.FileForStep;
import ch.ge.ael.enu.mediation.jway.model.FileForWorkflow;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToStatusChangeMapper;
import ch.ge.ael.enu.mediation.mapping.NewSuggestionToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep1Mapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep2Mapper;
import ch.ge.ael.enu.mediation.routes.processing.NewDemarcheToBrouillonReducer;
import ch.ge.ael.enu.mediation.service.technical.DeserializationService;
import ch.ge.ael.enu.mediation.service.technical.FormServicesRestInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Set;

import static ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus.EN_TRAITEMENT;
import static java.lang.String.format;

/**
 * Service de gestion des demarches :
 * <ul>
 *   <li>creation d'une demarche, a l'etat "brouillon", "deposee" ou "en traitement"</li>
 *   <li>changement d'etat d'une demarche.</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DemarcheService {

    private final DeserializationService deserializationService;
    private final FormServicesApi formServicesApi;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private final NewDemarcheToBrouillonReducer brouillonReducer = new NewDemarcheToBrouillonReducer();
    private final NewDemarcheToJwayMapper newDemarcheToJwayMapper = new NewDemarcheToJwayMapper();
    private final StatusChangeToJwayStep1Mapper statusChangeToJwayStep1Mapper = new StatusChangeToJwayStep1Mapper();
    private final StatusChangeToJwayStep2Mapper statusChangeToJwayStep2Mapper = new StatusChangeToJwayStep2Mapper();
    private final NewSuggestionToJwayMapper newSuggestionToJwayMapper = new NewSuggestionToJwayMapper();


    public void handleNewDemarche(Message message) {
        // deserialisation du message
        NewDemarche newDemarche = deserializationService.deserialize(message.getBody(), NewDemarche.class);
        log.info("newDemarche = {}", newDemarche);

        // validation metier du message
        Set<ConstraintViolation<NewDemarche>> errors = validator.validate(newDemarche);

        if(!errors.isEmpty()) {
            // Gestion des erreurs de validation
            ArrayList<String> texts = new ArrayList<>();
            errors.forEach(error -> texts.add(error.getPropertyPath() + ": " + error.getMessage() + ". Valeur passée: (" + error.getInvalidValue() + ")" ));
            throw new ValidationException(texts.toString());
        }

        handleNewDemarche(newDemarche);
    }

    public void handleNewDemarche(NewDemarche newDemarche) {
        DemarcheStatus status = newDemarche.getEtat();

        // creation dans FormServices de la demarche a l'etat de brouillon
        NewDemarche newDemarcheBrouillon = brouillonReducer.reduce(newDemarche);
        File brouillon = formServicesApi.postFile(
                newDemarcheToJwayMapper.map(newDemarcheBrouillon),
                newDemarche.getIdUsager());
        log.info("Demarche créée, uuid = [{}]", brouillon.getUuid());

        // passage dans FormServices a l'etat "deposee" (si pertinent)
        if (status == DEPOSEE || status == EN_TRAITEMENT) {
            StatusChange statusChange = new NewDemarcheToStatusChangeMapper(DEPOSEE).map(newDemarche);
            changeStatus(statusChange);
        }

        // passage dans FormServices a l'etat "en traitememt" (si pertinent)
        if (status == EN_TRAITEMENT) {
            StatusChange statusChange = new NewDemarcheToStatusChangeMapper(EN_TRAITEMENT).map(newDemarche);
            changeStatus(statusChange);
        }
    }

    public void handleStatusChange(Message message) {
        // deserialisation du message
        StatusChange statusChange = deserializationService.deserialize(message.getBody(), StatusChange.class);
        log.info("statusChange = {}", statusChange);

        // execution
        changeStatus(statusChange);
    }

    public void handleNewSuggestion(Message message) {
        // deserialisation du message
        NewSuggestion newSuggestion = deserializationService.deserialize(message.getBody(), NewSuggestion.class);
        log.info("newSuggestion = {}", newSuggestion);

        // validation metier du message
        Set<ConstraintViolation<NewSuggestion>> errors = validator.validate(newSuggestion);
        if(!errors.isEmpty()) {
            // Gestion des erreurs de validation
            ArrayList<String> texts = new ArrayList<>();
            errors.forEach(error -> texts.add(error.getPropertyPath() + ": " + error.getMessage() + ". Valeur passée: (" + error.getInvalidValue() + ")" ));
            throw new ValidationException(texts.toString());
        }

        // creation dans FormServices de la demarche a l'etat de pre-brouillon
        File file = newSuggestionToJwayMapper.map(newSuggestion);
        File createdFile = formServicesApi.postFile(file, newSuggestion.getIdUsager());
        log.info("Suggestion creee, uuid = [{}]", createdFile.getUuid());
    }

//    public File getDemarche(String demarcheId, String userId) {
//        final String SEARCH_PATH = "file/mine?name=%s&max=1&order=id&reverse=true";
//        String path = format(SEARCH_PATH, demarcheId);
//        List<File> demarches = formServices.get(path, userId,  new ParameterizedTypeReference<List<File>>(){});
//        if (demarches.isEmpty()) {
//            // si on ne trouve pas de demarche, on cherche avec le prefixe "DRAFT"
//            path = format(SEARCH_PATH, "(DRAFT)" + demarcheId);
//            demarches = formServices.get(path, userId,  new ParameterizedTypeReference<List<File>>(){});
//            if (demarches.isEmpty()) {
//                throw new ValidationException("Pas trouve la demarche \"" + demarcheId + "\"");
//            }
//        }
//        return demarches.get(0);
//    }

    public void changeStatus(StatusChange statusChange) {
        // validation metier du message
        Set<ConstraintViolation<StatusChange>> errors = validator.validate(statusChange);
        if(!errors.isEmpty()) {
            // Gestion des erreurs de validation
            ArrayList<String> texts = new ArrayList<>();
            errors.forEach(error -> texts.add(error.getPropertyPath() + ": " + error.getMessage() + ". Valeur passée: (" + error.getInvalidValue() + ")" ));
            throw new ValidationException(texts.toString());
        }

        String idUsager = statusChange.getIdUsager();

        // recuperation de l'uuid de la demarche dans FormServices
        File demarche = formServicesApi.getFile(statusChange.getIdDemarcheSiMetier(), statusChange.getIdUsager());
        String demarcheUuid = demarche.getUuid().toString();
        log.info("UUID demarche = [{}]", demarcheUuid);

        // etape 1 : changement du step dans FormServices
        formServicesApi.postFileStep(
                statusChangeToJwayStep1Mapper.map(statusChange),
                idUsager,
                demarche.getUuid()
        );

        // etape 2 : changement du workflow dans FormServices
        formServicesApi.postFileWorkflow(
                statusChangeToJwayStep2Mapper.map(statusChange),
                idUsager,
                demarche.getUuid()
        );
    }
}
