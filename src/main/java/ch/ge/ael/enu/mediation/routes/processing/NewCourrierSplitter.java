package ch.ge.ael.enu.mediation.routes.processing;

import ch.ge.ael.enu.mediation.metier.model.GedData;
import ch.ge.ael.enu.mediation.metier.model.NewCourrier;
import ch.ge.ael.enu.mediation.metier.model.NewCourrierDocument;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Scinde un courrier en "n" documents.
 */
public class NewCourrierSplitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewCourrierSplitter.class);

    public List<NewCourrierDocument> splitCourrier(NewCourrier newCourrier) {
        List<NewCourrierDocument> courriersDocs = new ArrayList<>();

        int index = 0;

        // une boucle "for" pre-Java 8, car on a besoin de l'indice du document
        for (NewDocument doc : newCourrier.getDocuments()) {
            NewCourrierDocument courrierDoc = new NewCourrierDocument();
            courrierDoc.setIdPrestation(newCourrier.getIdPrestation());
            courrierDoc.setIdUsager(newCourrier.getIdUsager());
            courrierDoc.setIdDemarcheSiMetier(newCourrier.getIdDemarcheSiMetier());
            courrierDoc.setLibelleCourrier(newCourrier.getLibelleCourrier());
            courrierDoc.setClefCourrier(newCourrier.getClef());
            courrierDoc.setLibelleDocument(doc.getLibelleDocument());
            courrierDoc.setIdDocumentSiMetier(doc.getIdDocumentSiMetier());
            courrierDoc.setMime(doc.getMime());
            courrierDoc.setContenu(doc.getContenu());

            if (doc.getGed() != null) {
                GedData ged = new GedData();
                ged.setFournisseur(doc.getGed().getFournisseur());
                ged.setVersion(doc.getGed().getVersion());
                ged.setIdDocument(doc.getGed().getIdDocument());
                ged.setAlgorithmeHash(doc.getGed().getAlgorithmeHash());
                ged.setHash(doc.getGed().getHash());
                courrierDoc.setGed(ged);
            }

            courrierDoc.setIndex(index++);
            courrierDoc.setNbDocuments(newCourrier.getDocuments().size());
            courriersDocs.add(courrierDoc);
        }

        LOGGER.info("Scission du courrier OK. Nombre de documents = {}", courriersDocs.size());
        return courriersDocs;
    }

}
