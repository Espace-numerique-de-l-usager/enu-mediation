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

import ch.ge.ael.enu.mediation.util.logging.BodyReducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static ch.ge.ael.enu.mediation.routes.communication.Header.CORRELATION_ID;

/**
 * Genere une trace du contenu d'un message recu de RabbitMQ.
 */
@Service
@Slf4j
public class MessageLoggingService {

    /**
     * Taille (en bytes Base 64) des fichiers au-dela de laquelle le contenu des fichiers n'est plus trace dans
     * son integralite dans la console et dans les fichiers de traces.
     */
    @Value("${app.logging.max-file-content-size}")
    private int maxFileContentSize;

    @Value("${app.formservices.url}")
    private String formServicesUrl;

    public void logMessage(Message message, boolean checkCorrelationId) {
        log.info("********************************");
        log.info("*** Message recu de RabbitMQ ***");
        log.info("********************************");

        String reducedBody = new BodyReducer(maxFileContentSize).reduceBody(message.getBody());
        log.info("Body {}, {}", reducedBody, message.getMessageProperties());

        if (checkCorrelationId && message.getMessageProperties().getHeader(CORRELATION_ID) == null) {
            log.warn("Le message ne contient pas l'en-tete \"{}\"", CORRELATION_ID);
        }
    }

    public void logJsonSent(HttpMethod method, String path, String content) {
        log.info("Appel REST a FormServices : {} {} with {}", method, formServicesUrl + "/" + path, content);
    }

}
