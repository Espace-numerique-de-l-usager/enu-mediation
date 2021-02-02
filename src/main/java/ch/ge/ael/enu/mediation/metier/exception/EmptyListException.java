package ch.ge.ael.enu.mediation.metier.exception;

/**
 * Exception lancee quand une liste est vide.
 */
public class EmptyListException extends ValidationException {

    public EmptyListException(String fieldName) {
        super("La liste \"" + fieldName + "\" ne peut pas etre vide");
    }

}
