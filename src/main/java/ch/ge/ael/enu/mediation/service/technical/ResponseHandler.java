package ch.ge.ael.enu.mediation.service.technical;

import ch.ge.ael.enu.mediation.business.domain.Response;
import ch.ge.ael.enu.mediation.business.domain.ResponseType;
import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static ch.ge.ael.enu.mediation.business.domain.ResponseType.KO;
import static ch.ge.ael.enu.mediation.business.domain.ResponseType.OK;
import static ch.ge.ael.enu.mediation.routes.communication.EnuMediaType.REPLY;
import static ch.ge.ael.enu.mediation.routes.communication.Header.CONTENT_TYPE;
import static ch.ge.ael.enu.mediation.routes.communication.Header.CORRELATION_ID;
import static ch.ge.ael.enu.mediation.routes.communication.Header.TECHNICAL_ERROR;

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

    @Value("${app.rabbitmq.reply.routing-key}")
    private String replyRoutingKey;

    @Value("${app.rabbitmq.dlq.exchange}")
    private String deadLetterExchange;

    @Value("${app.rabbitmq.dlq.routing-key}")
    private String deadLetterRoutingKey;

    public void handleOk(Message message) {
       log.info("Envoi a RabbitMQ d'un message de reussite");
        sendReplyMessage(OK, null, message);
    }

    public void handleKo(Exception e, Message message) {
        if (e instanceof ValidationException) {
            log.info("Envoi a RabbitMQ d'un message d'erreur client");
            sendReplyMessage(KO, e.getMessage(), message);
        } else {
            log.error("Envoi a RabbitMQ d'un message d'erreur serveur (DLQ), suite a ", e);
            message.getMessageProperties().setHeader(TECHNICAL_ERROR, e.getMessage());
            template.send(deadLetterExchange, "", message);
        }
    }

    private void sendReplyMessage(ResponseType type, String description, Message message) {
        Response response = new Response();
        response.setResultat(KO);
        response.setDescription(description);
        String jsonResponse = "Reponse mal formee";
        try {
            jsonResponse = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e2) {
            log.error("Erreur lors du traitement d'erreur", e2);
        }
        template.convertAndSend(replyExchange, replyRoutingKey, jsonResponse, msg -> {
            msg.getMessageProperties().setHeader(CONTENT_TYPE, REPLY);
            msg.getMessageProperties().setHeader(CORRELATION_ID, message.getMessageProperties().getHeader(CORRELATION_ID));
            return msg;
        });
    }

}
