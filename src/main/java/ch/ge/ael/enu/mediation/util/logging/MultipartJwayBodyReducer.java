package ch.ge.ael.enu.mediation.util.logging;

import static ch.ge.ael.enu.mediation.mapping.NewDocumentToJwayMapperProcessor.MULTIPART_BOUNDARY;

/**
 * Evite de mettre dans le fichier de trace un Body trop long, en tronquant les champs dont on sait qu'ils peuvent
 * etre tres long.
 * Cette classe est pour le cas d'un Body contenant un message de creation de document, a envoyer à Jway.
 */
public class MultipartJwayBodyReducer {

    private static final int SHOW_BEFORE = 50;

    private static final int SHOW_AFTER = 25;

    private static final String TRUNCATION = "... (champ tronque, car trop long) ...";

    private final int maxFileSize;

    public MultipartJwayBodyReducer(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * Tronque le Body s'il est trop long.
     * La partie suspecte d'etre trop longue se trouve entre "Content-Type: " et le "boundary" suivant
     */
    public byte[] reduceBody(byte[] body) {
        String sBody = new String(body);
        String toLog = sBody;

        int indexContentType = sBody.indexOf("Content-Type: ");
        if (indexContentType != -1) {
            int length = sBody.substring(indexContentType).indexOf(MULTIPART_BOUNDARY);   // taille de la partie suspecte
            if (length > getEffectiveMaxFileSize()) {
                toLog = sBody.substring(0, indexContentType + SHOW_BEFORE)
                        + TRUNCATION
                        + sBody.substring(indexContentType + length - SHOW_AFTER);
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
