package ch.ge.ael.enu.mediation.service.technical;

import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import ch.ge.ael.enu.mediation.exception.TechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Gestionnaire d'erreurs du consommateur enu-mediation.
 */
@Service
@Slf4j
public class ErrorHandler {

    /**
     * En-tete ajoute au message en cas d'erreur.
     * La valeur de cet en-tete est le message d'erreur.
     */
    public static final String ERROR_HEADER = "EnuFailureMessage";

    @Resource
    private AmqpTemplate template;

    @Value("${app.rabbitmq.deadletter.exchange}")
    private String deadLetterExchange;

    @Value("${app.rabbitmq.deadletter.routing-key}")
    private String deadLetterRoutingKey;

    public void handle(Exception e, Message message) {
        if (e instanceof ValidationException) {
            // erreur client : on renvoie le message dans la boite morte, avec le message d'erreur
            log.info("Erreur client lors du traitement du message: {}", e.getMessage());
            message.getMessageProperties().setHeader(ERROR_HEADER, e.getMessage());
            template.send(deadLetterExchange, deadLetterRoutingKey, message);
        } else if (e instanceof TechnicalException) {
            log.info(e.getMessage());
        } else {
            log.error("Erreur inattendue lors du traitement du message", e);
        }
    }

}
