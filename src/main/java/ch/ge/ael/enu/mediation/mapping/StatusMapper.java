package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.Status;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;

/**
 * Correspondance metier <-> Jway des statuts des demarches.
 */
public class StatusMapper {

    /**
     * metier -> Jway, au format String.
     */
    public String mapStringToJway(String demarcheStatus) {
        Status status = mapEnumToJway(DemarcheStatus.valueOf(demarcheStatus));
        return status == null ? null : status.name();
    }

    /**
     * metier -> Jway, au format enum.
     */
    public Status mapEnumToJway(DemarcheStatus demarcheStatus) {
        if (demarcheStatus == DemarcheStatus.BROUILLON) {
            return Status.START;
        } else if (demarcheStatus == DemarcheStatus.DEPOSEE) {
            return Status.VALIDATION;
        } else if (demarcheStatus == DemarcheStatus.EN_TRAITEMENT) {
            return Status.CORRECTION;
        } else if (demarcheStatus == DemarcheStatus.TERMINEE) {
            return Status.DONE;
        } else {
            return null;
        }
    }

    /**
     * Jway -> metier, au format String.
     */
    public String mapStringToMetier(String status) {
        DemarcheStatus demarcheStatus = mapEnumToMetier(Status.valueOf(status));
        return demarcheStatus == null ? null : demarcheStatus.name();
    }

    /**
     * Jway -> metier, au format enum.
     */
    public DemarcheStatus mapEnumToMetier(Status status) {
        if (status == Status.START) {
            return DemarcheStatus.BROUILLON;
        } else if (status == Status.VALIDATION) {
            return DemarcheStatus.DEPOSEE;
        } else if (status == Status.CORRECTION) {
            return DemarcheStatus.EN_TRAITEMENT;
        } else if (status == Status.DONE) {
            return DemarcheStatus.TERMINEE;
        } else {
            return null;
        }
    }

}
