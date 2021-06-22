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
package ch.ge.ael.enu.mediation.routes.communication;

public class EnuMediaType {

    public static final String NEW_DEMARCHE = "application/new-demarche-v1.0+json";

    public static final String NEW_SUGGESTION = "application/new-suggestion-v1.0+json";

    public static final String STATUS_CHANGE = "application/status-change-v1.0+json";

    public static final String NEW_DOCUMENT = "application/new-document-v1.0+json";

    public static final String NEW_COURRIER = "application/new-courrier-v1.0+json";

    public static final String REPLY = "application/reply-v1.0+json";

    public static final String BROUILLON_DELETION = "application/brouillon-deletion-v1.0+json";

    public static final String DOCUMENT_ACCESS = "application/document-access-v1.0+json";

    public static final String DOCUMENT_RECEPTION_MODE = "application/document-reception-mode-v1.0+json";

    private EnuMediaType() {
    }

}
