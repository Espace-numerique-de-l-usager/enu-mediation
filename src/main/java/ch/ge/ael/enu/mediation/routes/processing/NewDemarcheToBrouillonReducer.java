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
package ch.ge.ael.enu.mediation.routes.processing;

import ch.ge.ael.enu.business.domain.v1_0.NewDemarche;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus.BROUILLON;

/**
 * Pour une NewDemarche dans n'importe quel etat, cree une NewDemarche a l'etat de brouillon.
 */
public class NewDemarcheToBrouillonReducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDemarcheToBrouillonReducer.class);

    public NewDemarche reduce(NewDemarche newDemarche) {
        NewDemarche draft = new NewDemarche();

        draft.setEtat(BROUILLON.toString());
        draft.setIdPrestation(newDemarche.getIdPrestation());
        draft.setIdUsager(newDemarche.getIdUsager());

        if (newDemarche.getEtat().equals(BROUILLON.toString())) {
            // hack : si la demarche est un brouillon, on ajoute "DRAFT" au nom de la demarche.
            // Sans cette distinction, lors de la creation d'une demarche a l'etat "Deposee", l'application enu-backend
            // enverra coup sur coup 2 courriels a l'usager :
            // - un courriel (inutile) indiquant qu'un brouillon a ete cree
            // - un courriel (approprie) indiquant qu'une demarche a ete deposee
            draft.setIdDemarcheSiMetier("(DRAFT)" + newDemarche.getIdDemarcheSiMetier());
            draft.setUrlAction(newDemarche.getUrlAction());
        } else {
            draft.setIdDemarcheSiMetier(newDemarche.getIdDemarcheSiMetier());
        }

        LOGGER.info("Reduction a l'etat de brouillon OK");
        return draft;
    }

}
