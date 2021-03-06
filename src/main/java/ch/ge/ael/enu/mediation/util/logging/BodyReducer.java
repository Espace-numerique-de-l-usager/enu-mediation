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

import static ch.ge.ael.enu.mediation.util.logging.StringTruncationUtils.SHOW_AFTER;
import static ch.ge.ael.enu.mediation.util.logging.StringTruncationUtils.SHOW_BEFORE;
import static ch.ge.ael.enu.mediation.util.logging.StringTruncationUtils.TRUNCATION;

/**
 * Evite de mettre dans le fichier de trace un Body trop long, en tronquant les champs dont on sait qu'ils peuvent
 * etre tres longs.
 */
public class BodyReducer {

    private final int maxFileSize;

    public BodyReducer(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * Tronque le Body s'il est trop long.
     */
    public String reduceBody(byte[] body) {
        String sBody = new String(body);

        String toLog = sBody;

        int indexFieldNameContenu = sBody.indexOf("\"contenu\"");
        if (indexFieldNameContenu != -1) {
            int indexStartContenu = indexFieldNameContenu + "\"contenu\": \"".length();  // debut de la valeur du champ "contenu"
            int lengthContenu = sBody.substring(indexStartContenu).indexOf('\"');        // longueur du champ "contenu"
            if (lengthContenu > getEffectiveMaxFileSize()) {
                toLog = sBody.substring(0, indexStartContenu + SHOW_BEFORE)
                        + TRUNCATION
                        + sBody.substring(indexStartContenu + lengthContenu - SHOW_AFTER);
            }
        }

        return toLog;
    }

    /**
     * Rend maxFileSize, sauf si sa valeur est trop basse, auquel cas on rend une valeur plancher.
     * Un contenu de fichier plus petit que cette valeur sera affiche entierement.
     * Un contenu de fichier plus grand que cette valeur sera affiche de maniere tronquee.
     */
    private int getEffectiveMaxFileSize() {
        return Math.max(maxFileSize, SHOW_BEFORE + TRUNCATION.length() + SHOW_AFTER);
    }

}
