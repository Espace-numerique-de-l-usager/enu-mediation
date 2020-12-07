package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.FileForWorkflow;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatusChangeToJwayStep2Mapper {

    public FileForWorkflow map(StatusChange statusChange) {
        // TODO verifier la presence des champs

        FileForWorkflow file = new FileForWorkflow();
        file.setName(statusChange.getIdClientDemande());
        file.setWorkflowStatus(new StatusMapper().mapStringToJway(statusChange.getNouvelEtat()));
        if (statusChange.getTypeAction() != null) {
            StringBuilder sb = new StringBuilder()
                    .append(statusChange.getLibelleAction())
                    .append("|")
                    .append(statusChange.getTypeAction());
            file.setStepDescription(sb.toString());
            file.setToDate(statusChange.getEcheanceAction());
        }

        return file;
    }

}
