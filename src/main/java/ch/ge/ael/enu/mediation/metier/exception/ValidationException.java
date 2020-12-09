package ch.ge.ael.enu.mediation.metier.exception;

/**
 * Exception lancee quand un message contient des donnees invalides, par exemple manquantes,
 * mal formatees ou incoherentes.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String msg) {
        super(msg);
    }

}
