package ch.ge.ael.enu.mediation.util.logging;

/**
 * Evite de mettre dans le fichier de trace un Body trop long, en tronquant les champs dont on sait qu'ils peuvent
 * etre tres long.
 */
public class BodyReducer {

    private static final int SHOW_BEFORE = 10;

    private static final int SHOW_AFTER = 10;

    private static final String TRUNCATION = "... (champ tronque, car trop long) ...";

    private final int maxFileSize;

    public BodyReducer(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * Tronque le Body s'il est trop long.
     */
    public byte[] reduceBody(byte[] body) {
        String sBody = new String(body);

        String toLog = sBody;

        int indexFieldNameContenu = sBody.indexOf("\"contenu\"");
        if (indexFieldNameContenu != -1) {
            int indexStartContenu = indexFieldNameContenu + "\"contenu\": \"".length();  // debut de la valeur du champ "contenu"
            int lengthContenu = sBody.substring(indexStartContenu).indexOf("\"");        // longueur du champ "contenu"
            if (lengthContenu > getEffectiveMaxFileSize()) {
                toLog = sBody.substring(0, indexStartContenu + SHOW_BEFORE)
                        + TRUNCATION
                        + sBody.substring(indexStartContenu + lengthContenu - SHOW_AFTER);
            }
        }

        return toLog.getBytes();
    }

    /**
     * Rend maxFileSize, sauf si sa valeur est trop basse, auquel cas on rend une valeur plancher.
     * Un contenu de fichier plus petit que cette valeur sera affiche entierement.
     * Un contenu de fichier plus grand que cette valeur sera affiche de maniere tronquee.
     */
    private int getEffectiveMaxFileSize() {
        return Math.max(maxFileSize, SHOW_BEFORE + TRUNCATION.length() + SHOW_AFTER);
    }

}
