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
package ch.ge.ael.enu.mediation.jway.model;

import ch.ge.ael.enu.mediation.serialization.JwayLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class File {

    private Integer id = null;

    private UUID uuid = null;

    private String name = null;

    private User owner = null;

    private Application application = null;

    private String workflowStatus = null;

    private String status = null;

    private Form form = null;

    private String step = null;

    private String stepDescription = null;

    @JsonDeserialize(using = JwayLocalDateTimeDeserializer.class)
    private LocalDateTime stepDate = null;

    private Boolean validated = null;

    @JsonDeserialize(using = JwayLocalDateTimeDeserializer.class)
    private LocalDateTime fromDate = null;

    /**
     * Ne pas prendre une LocalDateTime, sinon HTTP 400 lors de l'envoi a Jway.
     */
//    @JsonDeserialize(using = JwayDateDeserializer.class)
//    private LocalDateTime toDate;
    private LocalDate toDate = null;

    private String redirectUrl = null;

    @JsonDeserialize(using = JwayLocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdate = null;
}
