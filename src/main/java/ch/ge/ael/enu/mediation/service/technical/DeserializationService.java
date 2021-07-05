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
package ch.ge.ael.enu.mediation.service.technical;

import ch.ge.ael.enu.mediation.exception.IllegalMessageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class DeserializationService {

    @Resource
    private ObjectMapper mapper;

    public <T> T deserialize(byte[] content, Class<T> clazz) {
        T object;
        try {
            object = mapper.readValue(content, clazz);
        } catch(Exception e) {
            log.info("Erreur lors de la deserialisation en un {} : {}", clazz.getCanonicalName(), e.getMessage());
            throw new IllegalMessageException("Erreur lors de la deserialisation du message JSON : " + e.getMessage());
        }
        return object;
    }

    public <T> T deserialize(byte[] content, com.fasterxml.jackson.core.type.TypeReference<T> valueTypeRef) {
        T object;
        try {
            object = mapper.readValue(content, valueTypeRef);
        } catch(Exception e) {
            log.info("Erreur lors de la deserialisation en un {} : {}", valueTypeRef.getType().getClass().getCanonicalName(), e.getMessage());
            throw new IllegalMessageException("Erreur lors de la deserialisation du message JSON : " + e.getMessage());
        }
        return object;
    }
}
