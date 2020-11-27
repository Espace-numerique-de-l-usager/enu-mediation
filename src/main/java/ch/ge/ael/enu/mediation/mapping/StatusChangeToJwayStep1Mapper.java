package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.FileForStep;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;

public class StatusChangeToJwayStep1Mapper {

    public FileForStep map(StatusChange statusChange) {
        // TODO verifier la presence des champs

//        File file = new File();
        FileForStep file = new FileForStep();
        file.setStep(new StatusMapper().mapStringToJway(statusChange.getNouvelEtat()));
//        file.setLastUpdate(statusChange.getDateNouvelEtat().atStartOfDay());
        file.setLastUpdate(statusChange.getDateNouvelEtat());

        return file;
    }

}
