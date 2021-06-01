package ch.ge.ael.enu.mediation.service.technical;

import ch.ge.ael.enu.mediation.util.logging.BodyReducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

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

        // TODO changer la signature en String plutot que byte[]
        byte[] reducedBody = new BodyReducer(maxFileContentSize).reduceBody(message.getBody());
        log.info(new String(reducedBody));
    }

    public void logJsonSent(HttpMethod method, String path, String content) {
        log.info("Appel REST a FormServices : {} {} with {}", method, formServicesUrl + "/" + path, content);
    }

}
