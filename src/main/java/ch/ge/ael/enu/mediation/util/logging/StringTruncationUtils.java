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
package ch.ge.ael.enu.mediation.util.logging;

public class StringTruncationUtils {

    static final int SHOW_BEFORE = 40;

    static final int SHOW_AFTER = 20;

    static final String TRUNCATION = " ... (champ tronque, car trop long) ... ";

    private StringTruncationUtils() {
    }

    public static String truncate(String s) {
        if (s == null) {
            return null;
        } else if (s.length() > SHOW_BEFORE + SHOW_AFTER + 30) {
            return s.substring(0, SHOW_BEFORE)
                    + TRUNCATION
                    + s.substring(s.length() - SHOW_AFTER - 1, s.length() -1);
        } else {
            return s;
        }
    }

}
