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
package ch.ge.ael.enu.mediation.exception;

/**
 * Erreur survenue durant le traitement du message et non imputable au message du producteur.
 * Habituellement il s'agit d'une anomalie a l'interieur de la mediation ou d'un appel au backend.
 */
public class TechnicalException extends RuntimeException {

    public TechnicalException(String msg) {
        super(msg);
    }

    public TechnicalException(Throwable cause) {
        super(cause);
    }

}
