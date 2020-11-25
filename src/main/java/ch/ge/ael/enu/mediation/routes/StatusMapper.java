package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.jway.model.Status;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;

public class StatusMapper {

    public String mapStringToJway(String demarcheStatus) {
        Status status = mapEnumToJway(DemarcheStatus.valueOf(demarcheStatus));
        return status == null ? null : status.name();
    }

    public Status mapEnumToJway(DemarcheStatus demarcheStatus) {
        if (demarcheStatus == DemarcheStatus.BROUILLON) {
            return Status.START;
        } else if (demarcheStatus == DemarcheStatus.SOUMISE) {
            return Status.VALIDATION;
        } else if (demarcheStatus == DemarcheStatus.EN_COURS) {
            return Status.CORRECTION;
        } else if (demarcheStatus == DemarcheStatus.TERMINEE) {
            return Status.DONE;
        } else {
            return null;
        }
    }

    public String mapStringToMetier(String status) {
        DemarcheStatus demarcheStatus = mapEnumToMetier(Status.valueOf(status));
        return demarcheStatus == null ? null : demarcheStatus.name();
    }

    public DemarcheStatus mapEnumToMetier(Status status) {
        if (status == Status.START) {
            return DemarcheStatus.BROUILLON;
        } else if (status == Status.VALIDATION) {
            return DemarcheStatus.SOUMISE;
        } else if (status == Status.CORRECTION) {
            return DemarcheStatus.EN_COURS;
        } else if (status == Status.DONE) {
            return DemarcheStatus.TERMINEE;
        } else {
            return null;
        }
    }

}
