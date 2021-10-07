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
package ch.ge.ael.enu.mediation.routes.processing;

import ch.ge.ael.enu.business.domain.v1_0.Courrier;
import ch.ge.ael.enu.business.domain.v1_0.CourrierDocument;
import ch.ge.ael.enu.business.domain.v1_0.GedData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Scinde un courrier en "n" documents.
 */
@Slf4j
public class NewCourrierSplitter {

    public List<CourrierDocument> splitCourrier(Courrier newCourrier) {
        List<CourrierDocument> courriersDocs = new ArrayList<>();

        int index = 0;

        // une boucle "for" pre-Java 8, car on a besoin de l'indice du document
        for (CourrierDocument doc : newCourrier.getDocuments()) {
            CourrierDocument courrierDoc = new CourrierDocument();
//            courrierDoc.setIdPrestation(newCourrier.getIdPrestation());
//            courrierDoc.setIdUsager(newCourrier.getIdUsager());
//            courrierDoc.setIdDemarcheSiMetier(newCourrier.getIdDemarcheSiMetier());
//            courrierDoc.setLibelleCourrier(newCourrier.getLibelleCourrier());
//            courrierDoc.setClefCourrier(newCourrier.getClef());
            courrierDoc.setLibelleDocument(doc.getLibelleDocument());
            courrierDoc.setIdDocumentSiMetier(doc.getIdDocumentSiMetier());
            courrierDoc.setMime(doc.getMime());
//            courrierDoc.setContenu(doc.getContenu());

            if (doc.getGed() != null) {
                GedData ged = new GedData();
                ged.setFournisseur(doc.getGed().getFournisseur());
                ged.setIdDocument(doc.getGed().getIdDocument());
                ged.setAlgorithmeHash(doc.getGed().getAlgorithmeHash());
                ged.setHash(doc.getGed().getHash());
                courrierDoc.setGed(ged);
            }

//            courrierDoc.setIndex(index++);
//            courrierDoc.setNbDocuments(newCourrier.getDocuments().size());
            courriersDocs.add(courrierDoc);
        }

        log.info("Scission du courrier OK. Nombre de documents = {}", courriersDocs.size());
        return courriersDocs;
    }

}
