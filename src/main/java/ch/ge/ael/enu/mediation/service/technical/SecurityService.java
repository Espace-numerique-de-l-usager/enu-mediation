/*
 *
 *  * Espace numerique de l'usager - enu-mediation
 *  *
 *  * Copyright (C) 2021 Republique et canton de Geneve
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU Affero General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Affero General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ch.ge.ael.enu.mediation.service.technical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityService {

    @Value("${app.prestation.simetier}")
    private String simetierByPrestationJson;

    private final ObjectMapper objectMapper;

    private Map<String, String> simetierByPrestation = null;

    @PostConstruct
    public void init() throws IOException {
        simetierByPrestation = objectMapper.readValue(simetierByPrestationJson.getBytes(), new TypeReference<Map<String,String>>() {});
        log.info("Table prestation -> SI metier : {}", simetierByPrestation);
    }

    /**
     * Rend le SI metier auquel appartient la prestation indiquee, ou null si pas trouve'.
     */
    public String getSimetier(String idPrestation) {
        return simetierByPrestation.get(idPrestation);
    }

}
