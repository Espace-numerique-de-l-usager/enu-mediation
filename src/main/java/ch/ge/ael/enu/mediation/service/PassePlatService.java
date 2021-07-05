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
package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.mediation.exception.TechnicalException;
import ch.ge.ael.enu.mediation.service.technical.SecurityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Service se contentant de republier dans RabbitMQ un message recu de RabbitMQ.
 * Concerne les messages enu-backend -> enu-mediation -> SI metier.
 */
@Service
@Slf4j
public class PassePlatService {

    @Resource
    private AmqpTemplate template;

    @Resource
    private ObjectMapper mapper;

    @Resource
    private SecurityService securityService;

    @Value("${app.rabbitmq.inverse.exchange}")
    private String inverseExchange;

    /**
     * Republie le message dans RabbitMQ.
     * Ajoute une clef de routage qui permettra a RabbitMQ de router vers le bon SI metier.
     */
    public void handle(Message message) {
        String routingKey = extractRoutingKey(message);
        template.send(inverseExchange, routingKey, message);
    }

    private String extractRoutingKey(Message message) {
        final String ID_PRESTATION = "idPrestation";
        try {
            Map<String, Object> deserializedMessage = mapper.readValue(message.getBody(), new TypeReference<Map<String, Object>>() {});
            String idPrestation = (String) deserializedMessage.get(ID_PRESTATION);
            return securityService.getSimetier(idPrestation);
        } catch (Exception e) {
            log.warn("Erreur lors de l'extraction de idPrestation du message [{}]", message);
            throw new TechnicalException(e);
        }
    }

}
