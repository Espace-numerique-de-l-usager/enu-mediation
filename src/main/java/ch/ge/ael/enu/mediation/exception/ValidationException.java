package ch.ge.ael.enu.mediation.exception;

/**
 * Exception lancee quand le message contient des donnees invalides.
 * Il s'agit donc d'une erreur commise par le producteur - non le consommateur - du message.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String msg) {
        super(msg);
    }

}
