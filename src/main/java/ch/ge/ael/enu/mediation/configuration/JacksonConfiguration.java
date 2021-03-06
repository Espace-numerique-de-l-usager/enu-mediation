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
package ch.ge.ael.enu.mediation.configuration;

import ch.ge.ael.enu.mediation.configuration.serialization.JwayLocalDateDeserializer;
import ch.ge.ael.enu.mediation.configuration.serialization.JwayLocalDateTimeDeserializer;
import ch.ge.ael.enu.mediation.configuration.serialization.MillisOrLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Our custom Jackson config for marshalling JSON to/from mediation
 */
@Configuration
public class JacksonConfiguration {

    /**
     * Currently a single configuration is enough for exchanges with Rabbit and FormServices.
     */
    @Bean(name = "json-jackson")
    public ObjectMapper jackson() {
        ObjectMapper jackson = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new MillisOrLocalDateTimeDeserializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new JwayLocalDateTimeDeserializer());
        javaTimeModule.addDeserializer(LocalDate.class, new JwayLocalDateDeserializer());
        jackson.registerModule(javaTimeModule);
        jackson.setDateFormat(new StdDateFormat());
        jackson.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jackson.enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);
        jackson.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jackson.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jackson.setTimeZone(TimeZone.getDefault());
        return jackson;
    }

}
