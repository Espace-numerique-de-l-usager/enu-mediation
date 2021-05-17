package ch.ge.ael.enu.mediation.util.invocation;

import org.apache.commons.lang3.StringUtils;

public class Precondition {

    public static void checkNotBlank(String value, String name) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Argument \"" + name + "\" is blank");
        }
    }

}
