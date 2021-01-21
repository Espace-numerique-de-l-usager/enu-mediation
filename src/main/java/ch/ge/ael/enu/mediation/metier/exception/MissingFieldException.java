package ch.ge.ael.enu.mediation.metier.exception;

/**
 * Exception lancee quand un champ obligatoire est absent dans un message.
 */
public class MissingFieldException extends ValidationException {

    public MissingFieldException(String fieldName) {
      super("Le champ \"" + fieldName + "\" manque");
    }

    public MissingFieldException(String fieldName, String complement) {
      super("Le champ \"" + fieldName + "\" manque. " + complement);
    }

}
