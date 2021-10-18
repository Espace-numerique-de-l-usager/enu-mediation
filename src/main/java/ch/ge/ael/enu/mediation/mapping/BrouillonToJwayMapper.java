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

import ch.ge.ael.enu.business.domain.v1_0.BrouillonDemarche;
import ch.ge.ael.enu.mediation.model.jway.Application;
import ch.ge.ael.enu.mediation.model.jway.File;
import ch.ge.ael.enu.mediation.model.jway.Status;
import ch.ge.ael.enu.mediation.model.jway.User;

public class BrouillonToJwayMapper {

    public File map(BrouillonDemarche newDemarche) {
        File file = new File();

        // hack : si la demarche est un brouillon, on ajoute "DRAFT" au nom de la demarche.
        // Sans cette distinction, lors de la creation d'une demarche a l'etat "Deposee", l'application enu-backend
        // enverra coup sur coup 2 courriels a l'usager :
        // - un courriel (inutile) indiquant qu'un brouillon a ete cree
        // - un courriel (approprie) indiquant qu'une demarche a ete deposee
        file.setName("(DRAFT)" + newDemarche.getIdDemarcheSiMetier());

        User owner = new User();
        owner.setName(newDemarche.getIdUsager());
        file.setOwner(owner);

        Application application = new Application();
        application.setName(newDemarche.getIdPrestation());
        file.setApplication(application);

        String jwayStatus = Status.START.toString();
        file.setWorkflowStatus(jwayStatus);
        file.setStatus(jwayStatus);

        return file;
    }
}
