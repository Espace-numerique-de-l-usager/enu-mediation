package ch.ge.ael.enu.mediation.util.mime;

public class MimeUtils {

    private MimeUtils() {
    }

    /**
     * Par exenmple : image/jpeg -> jpg
     */
    public static String getFileExtension(String mimeType) {
        if (mimeType.equals("application/pdf")) {
            return "pdf";
        } else if (mimeType.equals("image/jpeg")) {
            return "jpg";
        } else if (mimeType.equals("text/plain")) {
            return "txt";
        } else {
            throw new IllegalArgumentException("Le type mime [" + mimeType + " n'est pas prevu");
        }
    }

}
