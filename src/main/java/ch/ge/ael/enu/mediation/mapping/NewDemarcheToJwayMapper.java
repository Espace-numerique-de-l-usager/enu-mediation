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

import ch.ge.ael.enu.mediation.jway.model.Application;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.jway.model.Form;
import ch.ge.ael.enu.mediation.jway.model.FormUrl;
import ch.ge.ael.enu.mediation.jway.model.User;
import ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus;
import ch.ge.ael.enu.business.domain.v1_0.NewDemarche;

import java.util.ArrayList;

public class NewDemarcheToJwayMapper {

    public File map(NewDemarche newDemarche) {
        File file = new File();

        file.setName(newDemarche.getIdDemarcheSiMetier());

        User owner = new User();
        owner.setName(newDemarche.getIdUsager());
        file.setOwner(owner);

        Application application = new Application();
        application.setName(newDemarche.getIdPrestation());
        file.setApplication(application);

        String jwayStatus = new StatusMapper().mapEnumToJway(newDemarche.getEtat()).toString();
        file.setWorkflowStatus(jwayStatus);
        file.setStatus(jwayStatus);

        if (isBrouillon(newDemarche)) {
            if (newDemarche.getLibelleAction() != null) {
                file.setStepDescription("|" + newDemarche.getLibelleAction());
            }

            file.setToDate(newDemarche.getDateEcheanceAction());

            if (newDemarche.getUrlAction() != null) {
                Form form = new Form();
                file.setForm(form);
                form.setUrls(new ArrayList<>());
                FormUrl formUrl = new FormUrl();
                form.getUrls().add(formUrl);
                formUrl.setBaseUrl(newDemarche.getUrlAction().toString());
            }
        }

        return file;
    }

    private boolean isBrouillon(NewDemarche newDemarche) {
        return newDemarche.getEtat().equals(DemarcheStatus.BROUILLON);
    }

}
