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
package ch.ge.ael.enu.mediation;

import ch.ge.ael.enu.mediation.exception.IllegalMessageException;
import ch.ge.ael.enu.mediation.service.DemarcheService;
import ch.ge.ael.enu.mediation.service.DocumentService;
import ch.ge.ael.enu.mediation.service.technical.MessageLoggingService;
import ch.ge.ael.enu.mediation.service.technical.ResponseHandler;
import ch.ge.ael.enu.mediation.service.technical.SecurityService;
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
    private MessageLoggingService messageLoggingService;

    @Resource
    private DemarcheService demarcheService;

    @Resource
    private DocumentService courrierService;

    @Resource
    private SecurityService securityService;

    @Resource
    private ResponseHandler responseHandler;

    /**
     * Le point d'entree de l'application : consommation d'un message RabbitMQ.
     */
    @RabbitListener(queues = "${app.rabbitmq.main.queue}")
    public void consume(Message message) {
        messageLoggingService.logMessage(message);

        try {
            securityService.checkAuthorizedPrestation(message);
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
