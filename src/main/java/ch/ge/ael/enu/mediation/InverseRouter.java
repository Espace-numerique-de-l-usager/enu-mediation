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

import ch.ge.ael.enu.mediation.exception.TechnicalException;
import ch.ge.ael.enu.mediation.service.PassePlatService;
import ch.ge.ael.enu.mediation.service.technical.MessageLoggingService;
import ch.ge.ael.enu.mediation.service.technical.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static ch.ge.ael.enu.business.domain.v1_0.EnuMediaType.*;
import static ch.ge.ael.enu.mediation.model.Header.CONTENT_TYPE;

/**
 * Traitement des messages RabbitMQ du flux inverse ENU -> SI metier.
 * Fonctionne en mode passe-plat : lecture dans RabbitMQ du message de enu-backend, ecriture dans RabbitMQ
 * du meme message, pour le SI metier.
 */
@Component
@Slf4j
public class InverseRouter {

    @Resource
    private MessageLoggingService messageLoggingService;

    @Resource
    private PassePlatService passePlatService;

    @Resource
    private ResponseHandler responseHandler;

    private final List<String> validContentTypes = Arrays.asList(
            BROUILLON_ABANDON,
            DOCUMENT_ACCES,
            DOCUMENT_RECEPTION_MODE);

    /**
     * Consommation d'un message RabbitMQ du flux inverse.
     */
    @RabbitListener(queues = "${app.rabbitmq.backend-to-mediation.queue}")
    public void consume(Message message) {
        messageLoggingService.logMessage(message, false);

        try {
            route(message);
            log.info("Traitement OK");
        } catch (Exception e) {
            responseHandler.handleKo(e, message);
        }
    }

    /**
     * Attention : toutes les exceptions doivent etre de type technique, afin de mettre les messages dans
     * la DLQ. Il n'y a pas de message de reponse.
     */
    private void route(Message message) {
        String contentType = message.getMessageProperties().getHeader(CONTENT_TYPE);
        if (StringUtils.isBlank(contentType)) {
            throw new TechnicalException("L'en-tete \"" + CONTENT_TYPE + "\" manque dans le message");
        } else if (validContentTypes.contains(contentType)) {
            passePlatService.handle(message);
        } else {
            throw new TechnicalException(
                    "La valeur \"" + contentType + "\" de l'en-tête " + CONTENT_TYPE + " n'est pas prise en charge");
        }
        log.info("Traitement reussi");
    }

}
