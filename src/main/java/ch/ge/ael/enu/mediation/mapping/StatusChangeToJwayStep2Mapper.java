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

import ch.ge.ael.enu.mediation.jway.model.FileForWorkflow;
import ch.ge.ael.enu.mediation.business.domain.StatusChange;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatusChangeToJwayStep2Mapper {

    public FileForWorkflow map(StatusChange statusChange) {
        FileForWorkflow file = new FileForWorkflow();

        file.setName(statusChange.getIdDemarcheSiMetier());

        file.setWorkflowStatus(new StatusMapper().mapStringToJway(statusChange.getNouvelEtat()));

        if (statusChange.getTypeAction() != null) {
            StringBuilder sb = new StringBuilder()
                    .append(statusChange.getLibelleAction())
                    .append("|")
                    .append(statusChange.getTypeAction());
            file.setStepDescription(sb.toString());
            file.setToDate(statusChange.getDateEcheanceAction());
        }

        if (statusChange.getTypeAction() != null) {
            StringBuilder sb = new StringBuilder()
                    .append(statusChange.getLibelleAction())
                    .append("|")
                    .append(statusChange.getTypeAction());
            file.setStepDescription(sb.toString());
        }

        file.setToDate(statusChange.getDateEcheanceAction());

        return file;
    }

}
