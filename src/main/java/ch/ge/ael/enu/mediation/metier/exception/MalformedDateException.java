package ch.ge.ael.enu.mediation.metier.exception;

/**
 * Exception lancee quand une chaine de caracteres ne contient pas une date, ou ne contient pas une date au
 * format attendu.
 */
public class MalformedDateException extends ValidationException {

    public MalformedDateException(String value, String fieldName) {
        super("La valeur \"" + value + "\" du champ \"" + fieldName + "\" est incorrecte. Le format attendu est \"yyyy-MM-dd\".");
    }

}
