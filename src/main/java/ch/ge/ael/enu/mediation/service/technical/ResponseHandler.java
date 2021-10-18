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
import ch.ge.ael.enu.mediation.model.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static ch.ge.ael.enu.business.domain.v1_0.ResponseType.KO;
import static ch.ge.ael.enu.business.domain.v1_0.ResponseType.OK;
import static ch.ge.ael.enu.mediation.model.EnuMediaType.RESPONSE;
import static ch.ge.ael.enu.mediation.model.Header.CONTENT_TYPE;
import static ch.ge.ael.enu.mediation.model.Header.CORRELATION_ID;
import static ch.ge.ael.enu.mediation.model.Header.SI_METIER;
import static ch.ge.ael.enu.mediation.model.Header.TECHNICAL_ERROR;

/**
 * Gestionnaire d'erreurs du consommateur enu-mediation.
 */
@Service
@Slf4j
public class ResponseHandler {

    @Resource
    private AmqpTemplate template;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${app.rabbitmq.reply.exchange}")
    private String replyExchange;

    @Value("${app.rabbitmq.dlq.exchange}")
    private String deadLetterExchange;

    @Value("${app.rabbitmq.dlq.routing-key}")
    private String deadLetterRoutingKey;

    public void handleOk(Message originalMessage) {
        log.info("Envoi a RabbitMQ d'un message de reussite");
        sendReplyMessage(OK, null, originalMessage);
    }

    /**
     * Si erreur due au client, une reponse est renvoyee au client (queue de reponse).
     * Si erreur due a la mediation, le message est rejete dans la queue d'erreur (DLQ).
     */
    public void handleKo(Exception e, Message originalMessage) {
        if (e instanceof ValidationException) {
            log.info("Envoi a RabbitMQ du message d'erreur client suivant : {}", e.getMessage());
            sendReplyMessage(KO, e.getMessage(), originalMessage);
        } else {
            log.error("Envoi a RabbitMQ d'un message d'erreur serveur (DLQ), suite a ", e);
            originalMessage.getMessageProperties().setHeader(TECHNICAL_ERROR, e.getMessage());
            template.send(deadLetterExchange, "", originalMessage);
        }
    }

    private void sendReplyMessage(ResponseType type, String description, Message originalMessage) {
        Response response = new Response();
        response.setResultat(type);
        response.setDescription(description);
        String jsonResponse = "Reponse mal formee";
        try {
            jsonResponse = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e2) {
            log.error("Erreur lors du traitement d'erreur", e2);
        }
        String replyRoutingKey = originalMessage.getMessageProperties().getHeader(SI_METIER);
        template.convertAndSend(replyExchange, replyRoutingKey, jsonResponse, msg -> {
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
