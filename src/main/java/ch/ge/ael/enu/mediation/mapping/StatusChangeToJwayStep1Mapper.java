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

import ch.ge.ael.enu.mediation.jway.model.FileForStep;
import ch.ge.ael.enu.mediation.jway.model.Form;
import ch.ge.ael.enu.mediation.jway.model.FormUrl;
import ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus;
import ch.ge.ael.enu.business.domain.v1_0.StatusChange;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus.TERMINEE;

public class StatusChangeToJwayStep1Mapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FileForStep map(StatusChange statusChange) {
        FileForStep file = new FileForStep();

        file.setStep(new StatusMapper().mapEnumToJway(statusChange.getNouvelEtat()).toString());

        file.setLastUpdate(statusChange.getDateNouvelEtat().format(FORMATTER));

        String baseUrl = extractBaseUrl(statusChange);
        if (baseUrl != null) {
            Form form = new Form();
            file.setForm(form);
            form.setUrls(new ArrayList<>());
            FormUrl formUrl = new FormUrl();
            form.getUrls().add(formUrl);
            formUrl.setBaseUrl(baseUrl);
        }

        if (statusChange.getTypeAction() != null) {
            file.setStepDescription(statusChange.getLibelleAction() +
                    "|" +
                    statusChange.getTypeAction());
        }

        return file;
    }

    private boolean isNewStatus(StatusChange statusChange, DemarcheStatus status) {
        return statusChange.getNouvelEtat().equals(status);
    }

    /**
     * Extrait la valeur a mettre dans form.urls[0].baseUrl. Peut etre null
     */
    private String extractBaseUrl(StatusChange statusChange) {
        if (isNewStatus(statusChange, TERMINEE)) {
            return statusChange.getUrlRenouvellementDemarche().toString();
        } else {
            return statusChange.getUrlAction() == null ? null : statusChange.getUrlAction().toString();
        }
    }

}
