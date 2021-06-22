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
package ch.ge.ael.enu.mediation.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Donnees contenues dans un message JSON de creation d'une nouvelle suggestion de demarche.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewSuggestion {

    private String idPrestation = null;

    private String idUsager = null;

    /** Texte du bouton. */
    private String libelleAction = null;

    /** URL du bouton. */
    private String urlAction = null;

    private String dateEcheanceAction = null;

    /** Description complete de l'action. */
    private String descriptionAction = null;

    /** URL du livret de la prestation. */
    private String urlPrestation = null;

}
