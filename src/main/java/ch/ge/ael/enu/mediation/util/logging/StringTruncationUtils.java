package ch.ge.ael.enu.mediation.util.logging;

public class StringTruncationUtils {

    static final int SHOW_BEFORE = 40;

    static final int SHOW_AFTER = 20;

    static final String TRUNCATION = " ... (champ tronque, car trop long) ... ";

    private StringTruncationUtils() {
    }

    public static String truncate(String s) {
        if (s == null) {
            return null;
        } else if (s.length() > SHOW_BEFORE + SHOW_AFTER + 30) {
            return s.substring(0, SHOW_BEFORE)
                    + TRUNCATION
                    + s.substring(s.length() - SHOW_AFTER - 1, s.length() -1);
        } else {
            return s;
        }
    }

}
