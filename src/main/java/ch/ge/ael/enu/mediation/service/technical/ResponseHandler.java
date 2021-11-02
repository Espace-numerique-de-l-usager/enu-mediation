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

import ch.ge.ael.enu.business.domain.v1_0.Response;
import ch.ge.ael.enu.business.domain.v1_0.ResponseType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static ch.ge.ael.enu.business.domain.v1_0.EnuMediaType.RESPONSE;
import static ch.ge.ael.enu.mediation.model.Header.CONTENT_TYPE;
import static ch.ge.ael.enu.mediation.model.Header.CORRELATION_ID;

/**
 * Gestion des réponses de la médiation aux SI Métiers: OK / KO
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResponseHandler {

    private final RabbitTemplate defaultTemplate;
    private final RabbitTemplate dlxTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Les messages OK sont envoyés par le même exchange que les messages métier normaux
     */
    public void handleOk(Message originalMessage) throws JsonProcessingException {
        log.info("Envoi a RabbitMQ d'un message de reussite");

        defaultTemplate.convertAndSend(originalMessage.getMessageProperties().getReceivedRoutingKey(),
                objectMapper.writeValueAsString(Response.builder()
                        .resultat(ResponseType.OK)
                        .build()),
                msg -> {
                    msg.getMessageProperties().setHeader(CONTENT_TYPE, RESPONSE);
                    msg.getMessageProperties().setHeader(CORRELATION_ID, originalMessage.getMessageProperties().getHeader(CORRELATION_ID));
                    msg.getMessageProperties().setAppId(originalMessage.getMessageProperties().getAppId());
                    msg.getMessageProperties().setCorrelationId(originalMessage.getMessageProperties().getCorrelationId());
                    msg.getMessageProperties().setContentType(RESPONSE);
                    msg.getMessageProperties().setContentEncoding("UTF-8");
                    return msg;
                });
    }

    /**
     * Les erreurs sont rejetées dans la DLQ.
     */
    public void handleKo(Exception e, Message originalMessage) throws JsonProcessingException {
        log.info("Envoi a RabbitMQ d'un message d'erreur -> Dead Letter");

        dlxTemplate.convertAndSend(originalMessage.getMessageProperties().getReceivedRoutingKey(),
                objectMapper.writeValueAsString(Response.builder()
                        .resultat(ResponseType.KO)
                        .description(e.getMessage())
                        .build()),
                msg -> {
                    msg.getMessageProperties().setHeader(CONTENT_TYPE, RESPONSE);
                    msg.getMessageProperties().setHeader(CORRELATION_ID, originalMessage.getMessageProperties().getHeader(CORRELATION_ID));
                    msg.getMessageProperties().setAppId(originalMessage.getMessageProperties().getAppId());
                    msg.getMessageProperties().setCorrelationId(originalMessage.getMessageProperties().getCorrelationId());
                    msg.getMessageProperties().setContentType(RESPONSE);
                    msg.getMessageProperties().setContentEncoding("UTF-8");
                    return msg;
                });
    }
}
