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

import ch.ge.ael.enu.business.domain.v1_0.*;
import ch.ge.ael.enu.mediation.exception.NotFoundException;
import ch.ge.ael.enu.mediation.jway.model.*;
import ch.ge.ael.enu.mediation.mapping.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;


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

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final FormServicesApi formServicesApi;
    private final BrouillonToJwayMapper brouillonToJwayMapper = new BrouillonToJwayMapper();
    private final DemarcheDeposeeToJwayMapper demarcheDeposeeToJwayMapper = new DemarcheDeposeeToJwayMapper();

    public void handleDemarcheBrouillon(BrouillonDemarche brouillonDemarche) {
        File demarcheExistante;
        try {
            demarcheExistante = formServicesApi.getFile(brouillonDemarche.getIdDemarcheSiMetier(), brouillonDemarche.getIdUsager());
            log.warn("ECHEC création brouillon: existe déjà: {} pour idSimetier = {}", demarcheExistante.getUuid(), demarcheExistante.getName());
        } catch (NotFoundException e) {
            demarcheExistante = formServicesApi.postFile(
                    brouillonToJwayMapper.map(brouillonDemarche),
                    brouillonDemarche.getIdUsager());
            log.info("Demarche Deposee - Creation: {}", demarcheExistante);
        }
    }

    private void updateDemarcheStatus(String idDemarcheSiMetier,
                                      String idUsager,
                                      UUID fileUuid,
                                      LocalDateTime statusDate,
                                      Status newStatus,
                                      Form form,
                                      String stepDescription,
                                      LocalDate toDate) {
        FileForStep fileForStep = new FileForStep();
        fileForStep.setStep(newStatus.toString());
        fileForStep.setLastUpdate(statusDate.format(FORMATTER));
        if(form != null) {
            fileForStep.setForm(form);
        }
        if(stepDescription != null) {
            fileForStep.setStepDescription(stepDescription);
        }
        formServicesApi.postFileStep(fileForStep, idUsager, fileUuid);

        FileForWorkflow fileForWorkflow = new FileForWorkflow();
        fileForWorkflow.setName(idDemarcheSiMetier);
        fileForWorkflow.setWorkflowStatus(newStatus.toString());
        if(stepDescription != null) {
            fileForWorkflow.setStepDescription(stepDescription);
        }
        if(toDate != null) {
            fileForWorkflow.setToDate(toDate.format(FORMATTER));
        }
        formServicesApi.putFileWorkflow(fileForWorkflow, idUsager, fileUuid);
    }

    public void handleDemarcheDeposee(DemarcheDeposee demarcheDeposee) {
        File demarcheExistante;
        try {
            demarcheExistante = formServicesApi.getFile(demarcheDeposee.getIdDemarcheSiMetier(), demarcheDeposee.getIdUsager());
        } catch (NotFoundException e) {
            demarcheExistante = formServicesApi.postFile(
                    demarcheDeposeeToJwayMapper.map(demarcheDeposee),
                    demarcheDeposee.getIdUsager());
            log.info("Demarche Deposee - Creation: {}", demarcheExistante);
        }
        if(demarcheExistante.getWorkflowStatus().equals(Status.START.toString())) {
            updateDemarcheStatus(demarcheDeposee.getIdDemarcheSiMetier(),
                    demarcheDeposee.getIdUsager(),
                    demarcheExistante.getUuid(),
                    demarcheDeposee.getDateDepot(),
                    Status.VALIDATION, null, null, null);
        }
    }

    public void handleDemarcheEnTraitement(DemarcheEnTraitement demarcheEnTraitement) throws NotFoundException {
        File demarcheExistante;
        demarcheExistante = formServicesApi.getFile(demarcheEnTraitement.getIdDemarcheSiMetier(), demarcheEnTraitement.getIdUsager());
        if(demarcheExistante.getWorkflowStatus().equals(Status.VALIDATION.toString()) || demarcheExistante.getWorkflowStatus().equals(Status.CORRECTION.toString())) {
            updateDemarcheStatus(demarcheEnTraitement.getIdDemarcheSiMetier(),
                    demarcheEnTraitement.getIdUsager(),
                    demarcheExistante.getUuid(),
                    demarcheEnTraitement.getDateTraitement(),
                    Status.CORRECTION, null, null, null);
        } else {
            log.warn("ECHEC passage demarche en traitement: {}, status prédédent = {}", demarcheExistante.getName(), demarcheExistante.getWorkflowStatus());
        }
    }

    public void handleDemarcheActionRequise(DemarcheActionRequise demarcheActionRequise) throws NotFoundException {
        File demarcheExistante;
        demarcheExistante = formServicesApi.getFile(demarcheActionRequise.getIdDemarcheSiMetier(), demarcheActionRequise.getIdUsager());
        if(demarcheExistante.getWorkflowStatus().equals(Status.VALIDATION.toString()) || demarcheExistante.getWorkflowStatus().equals(Status.CORRECTION.toString())) {
            FormUrl formUrl = new FormUrl();
            formUrl.setBaseUrl(demarcheActionRequise.getUrlAction().toString());
            Form form = new Form();
            form.setUrls(new ArrayList<>());
            form.getUrls().add(formUrl);

            updateDemarcheStatus(demarcheActionRequise.getIdDemarcheSiMetier(),
                    demarcheActionRequise.getIdUsager(),
                    demarcheExistante.getUuid(),
                    demarcheActionRequise.getDateActionRequise(),
                    Status.CORRECTION,
                    form,
                    demarcheActionRequise.getLibelleAction() +
                            "|" +
                            demarcheActionRequise.getTypeAction(),
                    demarcheActionRequise.getDateEcheanceAction());
        } else {
            log.warn("ECHEC passage demarche en traitement: {}, status prédédent = {}", demarcheExistante.getName(), demarcheExistante.getWorkflowStatus());
        }
    }

    public void handleDemarcheTerminee(DemarcheTerminee demarcheTerminee) throws NotFoundException {
        File demarcheExistante;
        demarcheExistante = formServicesApi.getFile(demarcheTerminee.getIdDemarcheSiMetier(), demarcheTerminee.getIdUsager());
        if(demarcheExistante.getWorkflowStatus().equals(Status.CORRECTION.toString())) {
            FileForStep file = new FileForStep();
            file.setStep(Status.DONE.toString());
            file.setLastUpdate(demarcheTerminee.getDateCloture().format(FORMATTER));
            formServicesApi.postFileStep(file, demarcheTerminee.getIdUsager(), demarcheExistante.getUuid());

            FileForWorkflow fileForWorkflow = new FileForWorkflow();
            fileForWorkflow.setName(demarcheTerminee.getIdDemarcheSiMetier());
            fileForWorkflow.setWorkflowStatus(Status.VALIDATION.toString());
            formServicesApi.putFileWorkflow(fileForWorkflow, demarcheTerminee.getIdUsager(), demarcheExistante.getUuid());
        } else {
            log.warn("ECHEC passage demarche terminée: {}, status prédédent = {}", demarcheExistante.getName(), demarcheExistante.getWorkflowStatus());
        }
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

//    public void changeStatus(StatusChange statusChange) {
//        String idUsager = statusChange.getIdUsager();
//
//        // recuperation de l'uuid de la demarche dans FormServices
//        File demarche = formServicesApi.getFile(statusChange.getIdDemarcheSiMetier(), statusChange.getIdUsager());
//        String demarcheUuid = demarche.getUuid().toString();
//        log.info("UUID demarche = [{}]", demarcheUuid);
//
//        // etape 1 : changement du step dans FormServices
//        formServicesApi.postFileStep(
//                statusChangeToJwayStep1Mapper.map(statusChange),
//                idUsager,
//                demarche.getUuid()
//        );
//
//        // etape 2 : changement du workflow dans FormServices
//        formServicesApi.putFileWorkflow(
//                statusChangeToJwayStep2Mapper.map(statusChange),
//                idUsager,
//                demarche.getUuid()
//        );
//    }
}
