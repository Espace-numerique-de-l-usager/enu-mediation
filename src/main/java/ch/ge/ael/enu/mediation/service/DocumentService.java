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
import ch.ge.ael.enu.mediation.model.jway.File;
import ch.ge.ael.enu.mediation.mapping.CourrierDocumentToJwayMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service de gestion des documents :
 * <ul>
 *     <li>ajout d'un document a une demarche</li>
 *     <li>creation d'un courrier, lie ou non a une demarche.</li>
 * </ul>
 * Note sur les courriers :
 * dans Jway il n'a pas d'entite de courrier, il n'y a que "n" entites de documents ; chaque document
 * possede les donnees du courrier, ce qui permet d'identifier les documents constituant un meme courrier.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    /**
     * Types MIME (par ex. 'applicatiopn/pdf') de documents acceptes par la mediation.
     * Noter que si un type est ajoute a cette liste, le document n'est pas pour autant forcement accepte par
     * FormServices, car FormServivces a sa propre liste de types acceptes.
     */
    @Value("${app.document.mime-types}")
    private List<String> allowedMimeTypes;

    private final FormServicesApi formServicesApi;

    private String getDemarcheUuid(String idDemarcheSiMetier, String idUsager) throws NotFoundException {
        // recuperation dans FormServices de l'uuid de la demarche
        File demarche = formServicesApi.getFile(idDemarcheSiMetier, idUsager);
        String demarcheUuid = demarche.getUuid().toString();
        log.info("UUID demarche = [{}]", demarcheUuid);
        return demarcheUuid;
    }

    public void handleDocument(DocumentUsager newDocument) throws NotFoundException {
        String idUsager = newDocument.getIdUsager();
        String demarcheUuid = getDemarcheUuid(newDocument.getIdDemarcheSiMetier(),idUsager);

        // requete HEAD pour recuperer un jeton CSRF. Sans cette phase, on obtient une erreur 403 plus bas
        formServicesApi.postDocument(newDocument, demarcheUuid, idUsager);
    }

    public void handleDocument(DocumentUsagerBinaire newDocument) throws NotFoundException {
        String idUsager = newDocument.getIdUsager();
        String demarcheUuid = getDemarcheUuid(newDocument.getIdDemarcheSiMetier(),idUsager);

        // requete HEAD pour recuperer un jeton CSRF. Sans cette phase, on obtient une erreur 403 plus bas
        formServicesApi.postDocumentBinaire(newDocument, demarcheUuid, idUsager);
    }

    public void handleCourrier(Courrier courrier) throws NotFoundException {
        // ajout au courrier d'une clef technique. Cette clef sera affectee a chaque document constituant le
        // courrier et permettra donc de regrouper les documents du courrier
        courrier.setClef("Courrier-" + ZonedDateTime.now().toEpochSecond());
        final String demarcheUuid = getDemarcheUuid(courrier.getIdDemarcheSiMetier(),courrier.getIdUsager());
        formServicesApi.postCourrier(courrier, demarcheUuid, courrier.getIdUsager());
    }

    public void handleCourrier(CourrierBinaire courrierBinaire) throws NotFoundException {
        courrierBinaire.setClef("Courrier-" + ZonedDateTime.now().toEpochSecond());
        final String demarcheUuid = getDemarcheUuid(courrierBinaire.getIdDemarcheSiMetier(),courrierBinaire.getIdUsager());
        formServicesApi.postCourrierBinaire(courrierBinaire, demarcheUuid, courrierBinaire.getIdUsager());
    }

    public void handleCourrier(CourrierHorsDemarche courrierHorsDemarche) {

    }

    public void handleCourrier(CourrierHorsDemarcheBinaire object) {

    }
}
