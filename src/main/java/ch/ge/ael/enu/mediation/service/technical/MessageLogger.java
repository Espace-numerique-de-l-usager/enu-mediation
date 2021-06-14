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
public class MessageLogger {

    /**
     * Taille (en bytes Base 64) des fichiers au-dela de laquelle le contenu des fichiers n'est plus trace dans
     * son integralite dans la console et dans les fichiers de traces.
     */
    @Value("${app.logging.max-file-content-size}")
    private int maxFileContentSize;

    @Value("${app.formservices.url}")
    private String formServicesUrl;

    public void logMessage(Message message) {
        log.info("********************************");
        log.info("*** Message recu de RabbitMQ ***");
        log.info("********************************");

        String reducedBody = new BodyReducer(maxFileContentSize).reduceBody(message.getBody());
        log.info("Body {}, {}", reducedBody, message.getMessageProperties());

        if (message.getMessageProperties().getHeader(CORRELATION_ID) == null) {
            log.warn("Le message ne contient pas l'en-tete \"{}\"", CORRELATION_ID);
        }
    }

    public void logJsonSent(HttpMethod method, String path, String content) {
        log.info("Appel REST a FormServices : {} {} with {}", method, formServicesUrl + "/" + path, content);
    }

}
