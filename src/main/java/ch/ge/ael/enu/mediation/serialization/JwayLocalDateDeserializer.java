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
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

/**
 * Transforme en LocalDate une date reçue de FormServices, comme "2020-11-25T15:42:05.445+0000" ou
 * "2020-11-25T15:42:05.445+00:00".
 */
@Slf4j
public class JwayLocalDateDeserializer extends LocalDateDeserializer {

    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(ISO_LOCAL_DATE)
            .optionalStart()
                .appendLiteral('T')
                .append(ISO_LOCAL_TIME)
            .optionalEnd()
                .toFormatter();

    private static final DateTimeFormatter FORMAT = new DateTimeFormatterBuilder()
            // date/time
            .append(ISO_LOCAL_DATE_TIME)
            // offset (hh:mm - "+00:00" when it's zero)
            .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
            // offset (hh:mm - "+0000" when it's zero)
            .optionalStart().appendOffset("+HH:MM", "+0000").optionalEnd()
            // offset (hh - "+00" when it's zero)
            .optionalStart().appendOffset("+HH", "+00").optionalEnd()
            // offset (pattern "X" uses "Z" for zero offset)
            .optionalStart().appendPattern("X").optionalEnd()
            // create formatter
            .toFormatter();

    public JwayLocalDateDeserializer() {
        super(FORMAT);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        return LocalDate.parse(parser.getText(), FORMAT);
    }

}
