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

import ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus;
import ch.ge.ael.enu.business.domain.v1_0.NewDemarche;
import ch.ge.ael.enu.business.domain.v1_0.StatusChange;

import java.time.format.DateTimeFormatter;

import static ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.business.domain.v1_0.DemarcheStatus.EN_TRAITEMENT;

/**
 * Transforme une requete de creation de demarche (NewDemarche) en une requete de changement d'etat (StatusChange).
 * L'etat (par ex. SOUMIS) que doit avoir la requete de changement d'etat est donne en parametre du constructeur.
 */
public class NewDemarcheToStatusChangeMapper {

    private final DemarcheStatus demarcheStatus;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NewDemarcheToStatusChangeMapper(DemarcheStatus demarcheStatus) {
        this.demarcheStatus = demarcheStatus;
    }

    public StatusChange map(NewDemarche newDemarche) {
        StatusChange statusChange = new StatusChange();
        statusChange.setIdPrestation(newDemarche.getIdPrestation());
        statusChange.setIdUsager(newDemarche.getIdUsager());
        statusChange.setIdDemarcheSiMetier(newDemarche.getIdDemarcheSiMetier());
        statusChange.setNouvelEtat(demarcheStatus.name());
        if (demarcheStatus == DEPOSEE) {
            statusChange.setDateNouvelEtat(newDemarche.getDateDepot().toLocalDate());
        } else if (demarcheStatus == EN_TRAITEMENT) {
            statusChange.setDateNouvelEtat(newDemarche.getDateMiseEnTraitement().toLocalDate());
            statusChange.setLibelleAction(newDemarche.getLibelleAction());
            statusChange.setUrlAction(newDemarche.getUrlAction());
            statusChange.setTypeAction(newDemarche.getTypeAction());
            statusChange.setDateEcheanceAction(newDemarche.getDateEcheanceAction());
        }

        return statusChange;
    }

}
