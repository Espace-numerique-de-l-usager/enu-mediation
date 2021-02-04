package ch.ge.ael.enu.mediation.metier.exception;

/**
 * Exception lancee quand une liste comporte plus d'elements qu'autorise.
 */
public class TooLargeListException extends ValidationException {

    public TooLargeListException(String fieldName, int size, int maxSize) {
        super("La taille (" + size + ") de la liste \"" + fieldName + "\" excede la taille maximale autorisee ("
                + maxSize + ")");
    }

}
