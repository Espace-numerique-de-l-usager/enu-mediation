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
package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.Status;
import ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus;

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
