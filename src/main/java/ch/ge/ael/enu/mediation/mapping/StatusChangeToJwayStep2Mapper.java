package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatusChangeToJwayStep2Mapper {

    public File map(StatusChange statusChange) {
        // TODO verifier la presence des champs

        File file = new File();
        file.setName(statusChange.getIdClientDemande());
        file.setWorkflowStatus(new StatusMapper().mapStringToJway(statusChange.getNouvelEtat()));

        return file;
    }

}
