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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.JUnitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
public class JacksonConfigurationTest {

    @Test
    void contextLoads() {
        log.info("Spring Boot context OK");
    }

//    @Test
    void marshallJwayDatesAlt(@Autowired ObjectMapper jackson) throws JsonProcessingException {
        log.info("Jackson marshalling tests: java.time");
        LocalDate testDate = null;
        testDate = jackson.readValue("\"2021-09-03T00:00:000+0000\"", LocalDate.class);
        log.info("Test date alt = " + testDate);
    }

    @Test
    void marshallJwayDate(@Autowired ObjectMapper jackson) throws JsonProcessingException {
        log.info("Jackson marshalling alt tests: java.time");
        LocalDate testDate = null;
        testDate = jackson.readValue("\"2021-09-03T00:00:00.000+00:00\"", LocalDate.class);
        log.info("Test date = " + testDate);
    }

    @Test
    void marshallJwayDateTime(@Autowired ObjectMapper jackson) throws JsonProcessingException {
        log.info("Jackson marshalling alt tests: java.time");
        LocalDateTime testDate = null;
        testDate = jackson.readValue("\"2021-09-03T00:00:00.000+00:00\"", LocalDateTime.class);
        log.info("Test datetime = " + testDate);
    }
}
