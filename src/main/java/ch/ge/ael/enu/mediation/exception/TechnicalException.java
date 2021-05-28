package ch.ge.ael.enu.mediation.exception;

/**
 * Erreur survenue durant le traitement du message et non imputable au message du producteur.
 * Habituellement il s'agit d'une anomalie a l'interieur de la mediation ou d'un appel au backend.
 */
public class TechnicalException extends RuntimeException {

    public TechnicalException(String msg) {
        super(msg);
    }

    public TechnicalException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
