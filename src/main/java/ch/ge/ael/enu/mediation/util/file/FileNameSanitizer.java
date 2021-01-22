package ch.ge.ael.enu.mediation.util.file;

import org.apache.commons.lang3.StringUtils;

public class FileNameSanitizer {

    private String sanitizationRegex;

    public FileNameSanitizer(String sanitizationRegex) {
        if (StringUtils.isBlank(sanitizationRegex)) {
            throw new IllegalArgumentException("The regex in the constructor of "
                    + getClass().getSimpleName() + " is blank");
        }
        this.sanitizationRegex = sanitizationRegex;
    }

    public String sanitize(String fileName) {
        String ret = null;
        if (fileName != null) {
            ret = fileName.replaceAll(sanitizationRegex, "-").replaceAll("-+", "-");
        }
        return ret;
    }

}
