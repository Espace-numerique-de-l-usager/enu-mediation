package ch.ge.ael.enu.mediation.util.logging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MultipartJwayBodyReducerTest {

    @Test
    void short_body_should_not_be_truncated() {
        String body =
                "------FormBoundaryForEnuMediation\n" +
                "Content-Disposition: form-data; name=\"name\"\n" +
                "\n" +
                "Un manifeste|DOC-1234\n" +
                "------FormBoundaryForEnuMediation\n" +
                "Content-Disposition: form-data; name=\"files\"; filename=\"Un manifeste.jpg\"\n" +
                "Content-Type: image/jpeg\n" +
                "\n" +
                "AAAAAAAAAABBBBBBBBBB\n" +
                "------FormBoundaryForEnuMediation--";
        byte[] bytesReducedBody = new MultipartJwayBodyReducer(30).reduceBody(body.getBytes());

        assertThat(new String(bytesReducedBody)).isEqualTo(body);
    }

    @Test
    void long_body_with_large_max_file_size_should_not_be_truncated() {
        String body =
                "------FormBoundaryForEnuMediation\n" +
                "Content-Disposition: form-data; name=\"name\"\n" +
                "\n" +
                "Un manifeste|DOC-1234\n" +
                "------FormBoundaryForEnuMediation\n" +
                "Content-Disposition: form-data; name=\"files\"; filename=\"Un manifeste.jpg\"\n" +
                "Content-Type: image/jpeg\n" +
                "\n" +
                "AAAAAAAAAABBBBBBBBBBCCCCCCCCCCDDDDDDDDDDEEEEEEEEEE" +
                "FFFFFFFFFFGGGGGGGGGGHHHHHHHHHHIIIIIIIIIIJJJJJJJJJJ" +
                "KKKKKKKKKKLLLLLLLLLLMMMMMMMMMMNNNNNNNNNNOOOOOOOOOO\n" +
                "------FormBoundaryForEnuMediation--";
        byte[] bytesReducedBody = new MultipartJwayBodyReducer(1000).reduceBody(body.getBytes());

        assertThat(new String(bytesReducedBody)).isEqualTo(body);
    }

    @Test
    void long_body_should_be_truncated() {
        String body =
                "------FormBoundaryForEnuMediation\n" +
                "Content-Disposition: form-data; name=\"name\"\n" +
                "\n" +
                "Un manifeste|DOC-1234\n" +
                "------FormBoundaryForEnuMediation\n" +
                "Content-Disposition: form-data; name=\"files\"; filename=\"Un manifeste.jpg\"\n" +
                "Content-Type: image/jpeg\n" +
                "\n" +
                "AAAAAAAAAABBBBBBBBBBCCCCCCCCCCDDDDDDDDDDEEEEEEEEEE" +
                "FFFFFFFFFFGGGGGGGGGGHHHHHHHHHHIIIIIIIIIIJJJJJJJJJJ" +
                "KKKKKKKKKKLLLLLLLLLLMMMMMMMMMMNNNNNNNNNNOOOOOOOOOO\n" +
                "------FormBoundaryForEnuMediation--";
        byte[] bytesReducedBody = new MultipartJwayBodyReducer(100).reduceBody(body.getBytes());

        assertThat(new String(bytesReducedBody)).contains("AAAAAAAAAABBBB... (champ tronque, car trop long) ...NNNNNNNOOOOOOOOOO");
        }


    }
