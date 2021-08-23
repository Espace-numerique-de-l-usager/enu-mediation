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
package ch.ge.ael.enu.mediation.util.file;

import org.apache.commons.lang3.StringUtils;

public class FileNameSanitizer {

    private final String sanitizationRegex;

    public FileNameSanitizer(String sanitizationRegex) {
        if (StringUtils.isBlank(sanitizationRegex)) {
            throw new IllegalArgumentException("The regex in the constructor of "
                    + getClass().getSimpleName() + " is blank");
        }
        this.sanitizationRegex = sanitizationRegex;
    }

    public String sanitize(String fileName) {
        String ret = null;
        if (fileName != null) {
            ret = fileName.replaceAll(sanitizationRegex, "-").replaceAll("-+", "-");
        }
        return ret;
    }

}
