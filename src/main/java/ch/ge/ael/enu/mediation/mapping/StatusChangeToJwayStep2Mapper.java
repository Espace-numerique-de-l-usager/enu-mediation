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
import ch.ge.ael.enu.business.domain.v1_0.StatusChange;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class StatusChangeToJwayStep2Mapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FileForWorkflow map(StatusChange statusChange) {
        FileForWorkflow file = new FileForWorkflow();

        file.setName(statusChange.getIdDemarcheSiMetier());

        file.setWorkflowStatus(new StatusMapper().mapEnumToJway(statusChange.getNouvelEtat()).toString());

        if (statusChange.getTypeAction() != null) {
            file.setStepDescription(statusChange.getLibelleAction() +
                    "|" +
                    statusChange.getTypeAction());
            file.setToDate(statusChange.getDateEcheanceAction().format(FORMATTER));
        }

        return file;
    }

}
