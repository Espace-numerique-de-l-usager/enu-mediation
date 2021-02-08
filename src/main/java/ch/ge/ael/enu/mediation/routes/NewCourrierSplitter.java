package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.metier.model.NewCourrier;
import ch.ge.ael.enu.mediation.metier.model.NewCourrierDocument;
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
//        LOGGER.info("newCourrier = {}", newCourrier);
        LOGGER.info("Dans {}", getClass().getSimpleName());

        List<NewCourrierDocument> courriersDocs = new ArrayList<>();

        newCourrier.getDocuments()
                .forEach(doc -> {
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
                    courriersDocs.add(courrierDoc);
                });

//        LOGGER.info("courriersDocs = {}", courriersDocs);
        return courriersDocs;
    }

}
