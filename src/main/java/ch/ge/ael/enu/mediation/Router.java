package ch.ge.ael.enu.mediation;

import ch.ge.ael.enu.mediation.exception.IllegalMessageException;
import ch.ge.ael.enu.mediation.service.DemarcheService;
import ch.ge.ael.enu.mediation.service.DocumentService;
import ch.ge.ael.enu.mediation.service.technical.MessageLogger;
import ch.ge.ael.enu.mediation.service.technical.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static ch.ge.ael.enu.mediation.routes.communication.EnuMediaType.NEW_COURRIER;
import static ch.ge.ael.enu.mediation.routes.communication.EnuMediaType.NEW_DEMARCHE;
import static ch.ge.ael.enu.mediation.routes.communication.EnuMediaType.NEW_DOCUMENT;
import static ch.ge.ael.enu.mediation.routes.communication.EnuMediaType.NEW_SUGGESTION;
import static ch.ge.ael.enu.mediation.routes.communication.EnuMediaType.STATUS_CHANGE;
import static ch.ge.ael.enu.mediation.routes.communication.Header.CONTENT_TYPE;

@Component
@Slf4j
public class Router {

    @Resource
    private MessageLogger messageLogger;

    @Resource
    private DemarcheService demarcheService;

    @Resource
    private DocumentService courrierService;

    @Resource
    private ResponseHandler responseHandler;

    /**
     * Le point d'entree de l'application : consommation d'un message RabbitMQ.
     */
    @RabbitListener(queues = "${app.rabbitmq.main.queue}")
    public void consume(Message message) {
        messageLogger.logMessage(message);

        try {
            route(message);
            log.info("Traitement OK");
        } catch (Exception e) {
            responseHandler.handleKo(e, message);
        }
    }

    private void route(Message message) {
        String contentType = message.getMessageProperties().getHeader(CONTENT_TYPE);
        if (StringUtils.isBlank(contentType)) {
            throw new IllegalMessageException("L'en-tete \"" + CONTENT_TYPE + "\" manque dans le message");
        } else if (contentType.equals(NEW_DEMARCHE)) {
            demarcheService.handleNewDemarche(message);
        } else if (contentType.equals(STATUS_CHANGE)) {
            demarcheService.handleStatusChange(message);
        } else if (contentType.equals(NEW_SUGGESTION)) {
            demarcheService.handleNewSuggestion(message);
        } else if (contentType.equals(NEW_DOCUMENT)) {
            courrierService.handleNewDocument(message);
        } else if (contentType.equals(NEW_COURRIER)) {
            courrierService.handleNewCourrier(message);
        } else {
            throw new IllegalMessageException(
                    "La valeur \"" + contentType + "\" de l'en-tête " + CONTENT_TYPE + " n'est pas prise en charge");
        }
        responseHandler.handleOk(message);
    }

}
