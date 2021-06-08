package ch.ge.ael.enu.mediation.util.logging;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BodyReducerTest {

    @Test
    void short_body_should_not_be_truncated() {
        String body = "{\"mime\": \"text/plain\", \"contenu\": \"U2FsdXQgY2FtYXJhZGUgIQ==\"}";
        String bytesReducedBody = new BodyReducer(30).reduceBody(body.getBytes());

        assertThat(bytesReducedBody).isEqualTo(body);
    }

    @Test
    void long_body_should_be_truncated() {
        String body = "{\"mime\": \"text/plain\", \"contenu\": \"77+977+977+977+9ABBKRklGAAEBAQBgAGAAAO+/ve+/vAGAAAOAGAAAOQAsRXhpZgAATU0AKgAAAAgAAQExAAIAAAAKAAAAGgAAAABHcmVlbnN\"}";
        String bytesReducedBody = new BodyReducer(30).reduceBody(body.getBytes());

        assertThat(bytesReducedBody).isEqualTo("{\"mime\": \"text/plain\", \"contenu\": \"77+977+977+977+9ABBKRklGAAEBAQBgAGAAAO+/ ... (champ tronque, car trop long) ... KAAAAGgAAAABHcmVlbnN\"}");
    }

}
