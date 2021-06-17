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
package ch.ge.ael.enu.mediation.business.exception;

/**
 * Exception lancee quand une chaine de caracteres ne contient pas une date, ou ne contient pas une date au
 * format attendu.
 */
public class MalformedDateException extends ValidationException {

    public MalformedDateException(String value, String fieldName) {
        super("La valeur \"" + value + "\" du champ \"" + fieldName + "\" est incorrecte. Le format attendu est \"yyyy-MM-dd\"");
    }

}
