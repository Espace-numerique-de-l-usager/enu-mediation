package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;

public class StatusChangeToJwayStep1Mapper {

    public File map(StatusChange statusChange) {
        // TODO verifier la presence des champs

        File file = new File();
        file.setStep(new StatusMapper().mapStringToJway(statusChange.getNouvelEtat()));
        file.setLastUpdate(statusChange.getDateNouvelEtat().atStartOfDay());

        return file;
    }

}
