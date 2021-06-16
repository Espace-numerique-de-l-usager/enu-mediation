package ch.ge.ael.enu.mediation.routes.communication;

import org.springframework.http.HttpHeaders;

/**
 * Pour les autres en-tetes, notamment HTTP, voir {}@link {@link HttpHeaders}.
 */
public class Header {

    /**
     * Contexte : RabbitMQ.
     * Le contenu du message, habituellement du JSON.
     */
    public static final String CONTENT_TYPE = "ContentType";

    /**
     * Contexte : RabbitMQ.
     * L'identifiant de correlation, permettant au producteur de faire le lien entre le message de reponse
     * de la mediation et son message d'origine.
     */
    public static final String CORRELATION_ID = "CorrelationId";

    /**
     * Contexte : RabbitMQ.
     * L'identifiant du SI metier producteur du message d'origine.
     * Cet identifiant permet a la mediation de router le message de reponse vers la queue du bon SI metier.
     */
    public static final String SI_METIER = "SiMetier";

    /**
     * Contexte : RabbitMQ.
     * Permet d'enrichir le message rejete dans la DLQ apres une erreur technique.
     */
    public static final String TECHNICAL_ERROR = "TechnicalError";

    /**
     * Contexte : HTTP.
     * Necessaire pour certains appels REST à FormServices.
     */
    public static final String X_CSRF_TOKEN = "X-CSRF-Token";

    /**
     * Contexte : HTTP.
     * Permet d'appeler les services REST de FormServices en tant que le bon utilisateur.
     */
    public static final String REMOTE_USER = "remote_user";

    private Header() {
    }

}
