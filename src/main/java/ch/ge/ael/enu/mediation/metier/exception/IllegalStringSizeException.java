package ch.ge.ael.enu.mediation.metier.exception;

/**
 * Exception lancee quand une chaine de caracteres est trop courte ou trop longue.
 */
public class IllegalStringSizeException extends ValidationException {

    public IllegalStringSizeException(String value, int minSize, int maxSize, String fieldName) {
        super("La valeur \"" + truncateIfTooLong(value) + "\" du champ \"" + fieldName + "\" est d'une taille incorrecte."
                + " Taille attendue : entre " + minSize + " et " + maxSize + " caracteres.");
    }

    /**
     * Si un saligaud envoie une chaine de taille 100'000'000, nous ne voulons pas creer a notre tour un
     * message d'erreur de cette taille.
     */
    private static String truncateIfTooLong(String s) {
        if (s.length() > 1000) {
            return s.substring(0, 1000) + " (...)";
        } else {
            return s;
        }
    }

}
