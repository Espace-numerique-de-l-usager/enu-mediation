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
package ch.ge.ael.enu.mediation.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Transforme en LocalDateTime une date reçue de FormServices, comme "2020-11-25T15:42:05.445+0000" ou
 * "2020-11-25T15:42:05.445+00:00".
 */
public class JwayDateDeserializer extends StdDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMAT_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+0000", Locale.FRENCH);

    private static final DateTimeFormatter FORMAT_2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00", Locale.FRENCH);

    public JwayDateDeserializer() {
        this(null);
    }

    public JwayDateDeserializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        // on essaie avec un format, puis avec l'autre
        try {
          return LocalDateTime.parse(parser.getText(), FORMAT_1);
        } catch (Exception e) {
          return LocalDateTime.parse(parser.getText(), FORMAT_2);
        }
    }

}
