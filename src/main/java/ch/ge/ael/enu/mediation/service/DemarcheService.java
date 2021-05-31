package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.mediation.business.domain.DemarcheStatus;
import ch.ge.ael.enu.mediation.business.domain.NewDemarche;
import ch.ge.ael.enu.mediation.business.domain.NewSuggestion;
import ch.ge.ael.enu.mediation.business.domain.StatusChange;
import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import ch.ge.ael.enu.mediation.business.validation.NewDemarcheValidator;
import ch.ge.ael.enu.mediation.business.validation.NewSuggestionValidator;
import ch.ge.ael.enu.mediation.business.validation.StatusChangeValidator;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static ch.ge.ael.enu.mediation.business.domain.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.mediation.business.domain.DemarcheStatus.EN_TRAITEMENT;
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
public class DemarcheService {

    @Resource
    private DeserializationService deserializationService;

    @Resource
    private FormServicesRestInvoker formServices;

    private final NewDemarcheValidator newDemarcheValidator = new NewDemarcheValidator();

    private final StatusChangeValidator statusChangeValidator = new StatusChangeValidator();

    private final NewSuggestionValidator newSuggestionValidator = new NewSuggestionValidator();

    private final NewDemarcheToBrouillonReducer reducer = new NewDemarcheToBrouillonReducer();

    private final NewDemarcheToJwayMapper newDemarcheToJwayMapper = new NewDemarcheToJwayMapper();

    private final StatusChangeToJwayStep1Mapper statusChangeToJwayStep1Mapper = new StatusChangeToJwayStep1Mapper();

    private final StatusChangeToJwayStep2Mapper statusChangeToJwayStep2Mapper = new StatusChangeToJwayStep2Mapper();

    private final NewSuggestionToJwayMapper newSuggestionToJwayMapper = new NewSuggestionToJwayMapper();

    public void handleNewDemarche(Message message) {
        // deserialisation du message
        NewDemarche newDemarche = deserializationService.deserialize(message.getBody(), NewDemarche.class);
        log.info("newDemarche = {}", newDemarche);

        // validation metier du message
        newDemarcheValidator.validate(newDemarche);
        DemarcheStatus status = DemarcheStatus.valueOf(newDemarche.getEtat());

        // creation dans FormServices de la demarche a l'etat de brouillon
        NewDemarche newDemarcheBrouillon = reducer.reduce(newDemarche);
        File file = newDemarcheToJwayMapper.map(newDemarcheBrouillon);
        ParameterizedTypeReference<File> typeReference = new ParameterizedTypeReference<File>() {};
        File createdFile = formServices.post("alpha/file", file, newDemarche.getIdUsager(), typeReference);
        log.info("Demarche creee, uuid = [{}]", createdFile.getUuid());

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
        newSuggestionValidator.validate(newSuggestion);

        // creation dans FormServices de la demarche a l'etat de pre-brouillon
        File file = newSuggestionToJwayMapper.map(newSuggestion);
        ParameterizedTypeReference<File> typeReference = new ParameterizedTypeReference<File>() {};
        File createdFile = formServices.post("alpha/file", file, newSuggestion.getIdUsager(), typeReference);
        log.info("Suggestion creee, uuid = [{}]", createdFile.getUuid());
    }

    public File getDemarche(String demarcheId, String userId) {
        String path = format("file/mine?name=%s&max=1&order=id&reverse=true", demarcheId);
        List<File> demarches = formServices.get(path, userId,  new ParameterizedTypeReference<List<File>>(){});
        if (demarches.isEmpty()) {
            throw new ValidationException("Pas trouve la demarche \"" + demarcheId + "\"");
        }
        return demarches.get(0);
    }

    private void changeStatus(StatusChange statusChange) {
        // validation metier du message
        statusChangeValidator.validate(statusChange);
        String idUsager = statusChange.getIdUsager();

        // recuperation de l'uuid de la demarche dans FormServices
        File demarche = getDemarche(statusChange.getIdDemarcheSiMetier(), statusChange.getIdDemarcheSiMetier());
        String demarcheUuid = demarche.getUuid().toString();
        log.info("UUID demarche = [{}]", demarcheUuid);

        // etape 1 : changement du step dans FormServices
        FileForStep fileForStep = statusChangeToJwayStep1Mapper.map(statusChange);
        String path = format("alpha/file/%s/step", demarcheUuid);
        ParameterizedTypeReference<File> typeReference = new ParameterizedTypeReference<File>() {};
        formServices.post(path, fileForStep, idUsager, typeReference);

        // etape 2 : changement du workflow dans FormServices
        FileForWorkflow fileForWorkflow = statusChangeToJwayStep2Mapper.map(statusChange);
        path = format("alpha/file/%s", demarcheUuid);
        formServices.put(path, fileForWorkflow, idUsager, typeReference);
    }

}
