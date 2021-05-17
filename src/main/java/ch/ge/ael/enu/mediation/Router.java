package ch.ge.ael.enu.mediation;

import ch.ge.ael.enu.mediation.exception.IllegalMessageException;
import ch.ge.ael.enu.mediation.service.CourrierService;
import ch.ge.ael.enu.mediation.service.DemarcheService;
import ch.ge.ael.enu.mediation.service.technical.ErrorHandler;
import ch.ge.ael.enu.mediation.service.technical.MessageLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static ch.ge.ael.enu.mediation.routes.http.Header.CONTENT_TYPE;
import static ch.ge.ael.enu.mediation.routes.http.MediaType.NEW_COURRIER;
import static ch.ge.ael.enu.mediation.routes.http.MediaType.NEW_DEMARCHE;

@Component
@Slf4j
public class Router {

    // TODO mettre dans la configuration
    private static final String QUEUE_NAME = "simetier1-to-enu-main-q";

    @Resource
    private MessageLogger messageLogger;

    @Resource
    private DemarcheService demarcheService;

    @Resource
    private CourrierService courrierService;

    @Resource
    private ErrorHandler errorHandler;

    @RabbitListener(queues = QUEUE_NAME)
    public void consume(Message message) {
        messageLogger.logMessage(message);

        try {
            // traitement du message
            route(message);
        } catch (Exception e) {
            errorHandler.handle(e, message);
        }
    }

    private void route(Message message) {
        String contentType = message.getMessageProperties().getHeader(CONTENT_TYPE);
        if (StringUtils.isBlank(contentType)) {
            throw new IllegalMessageException("L'en-tete " + CONTENT_TYPE + " manque dans le message");
        } else if (contentType.equals(NEW_DEMARCHE)) {
            demarcheService.handle(message);
        } else if (contentType.equals(NEW_COURRIER)) {
            courrierService.handle(message);
        } else {
            throw new IllegalMessageException(
                    "La valeur \"" + contentType + "\" de 'en-tête " + CONTENT_TYPE + " n'est pas prise en charge");
        }
    }

}
