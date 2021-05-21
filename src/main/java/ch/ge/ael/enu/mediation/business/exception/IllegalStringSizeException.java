package ch.ge.ael.enu.mediation.business.exception;

/**
 * Exception lancee quand une chaine de caracteres est trop courte ou trop longue.
 */
public class IllegalStringSizeException extends ValidationException {

    public IllegalStringSizeException(String value, int minSize, int maxSize, String fieldName) {
        super("La valeur \"" + truncateIfTooLong(value) + "\" du champ \"" + fieldName + "\" est d'une taille"
                + " incorrecte (" + value.length() + " caracteres)."
                + " Taille autorisee : entre " + minSize + " et " + maxSize + " caracteres");
    }

    /**
     * Si un saligaud envoie une chaine de taille 100'000'000, nous ne voulons pas creer a notre tour un
     * message d'erreur de cette taille.
     */
    private static String truncateIfTooLong(String s) {
        final int MAX_LENGTH = 1000;
        if (s.length() > MAX_LENGTH) {
            return s.substring(0, MAX_LENGTH) + " (...)";
        } else {
            return s;
        }
    }

}
