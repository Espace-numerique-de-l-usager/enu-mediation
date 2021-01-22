package ch.ge.ael.enu.mediation.util.file;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileNameSanitizerTest {

    public static final String REGEX = "[^a-zA-Z0-9âàçéèêôùÂÀÉÈ\\.]";

    @Test
    void sanitize_file_name_with_slashes() {
        String sanitized = new FileNameSanitizer(REGEX).sanitize("abc___ABCéç%%&&r$x/y\\z.a.b(ô ê ù)Z");
        assertThat(sanitized).isEqualTo("abc-ABCéç-r-x-y-z.a.b-ô-ê-ù-Z");
    }

}
