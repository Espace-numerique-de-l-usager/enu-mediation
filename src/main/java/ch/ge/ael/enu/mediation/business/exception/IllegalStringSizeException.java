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
 * Exception lancee quand une chaine de caracteres est trop courte ou trop longue.
 */
public class IllegalStringSizeException extends ValidationException {

    public IllegalStringSizeException(String value, int minSize, int maxSize, String fieldName) {
        super("La valeur \"" + truncateIfTooLong(value) + "\" du champ \"" + fieldName + "\" est d'une taille"
                + " incorrecte (" + value.length() + " caracteres)."
                + " Taille autorisee : entre " + minSize + " et " + maxSize + " caracteres");
    }

    /**
     * Si un saligaud envoie une chaine de taille 100'000'000, nous ne voulons pas creer a notre tour un
     * message d'erreur de cette taille.
     */
    private static String truncateIfTooLong(String s) {
        final int MAX_LENGTH = 1000;
        if (s.length() > MAX_LENGTH) {
            return s.substring(0, MAX_LENGTH) + " (...)";
        } else {
            return s;
        }
    }

}
