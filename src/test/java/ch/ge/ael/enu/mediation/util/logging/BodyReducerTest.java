package ch.ge.ael.enu.mediation.util.logging;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BodyReducerTest {

    @Test
    void short_body_should_not_be_truncated() {
        String body = "{\"mime\": \"text/plain\", \"contenu\": \"U2FsdXQgY2FtYXJhZGUgIQ==\"}";
        byte[] bytesReducedBody = new BodyReducer(30).reduceBody(body.getBytes());

        assertThat(new String(bytesReducedBody)).isEqualTo(body);
    }

    @Test
    void long_body_should_be_truncated() {
        String body = "{\"mime\": \"text/plain\", \"contenu\": \"77+977+977+977+9ABBKRklGAAEBAQBgAGAAAO+/ve+/vQAsRXhpZgAATU0AKgAAAAgAAQExAAIAAAAKAAAAGgAAAABHcmVlbnN\"}";
        byte[] bytesReducedBody = new BodyReducer(30).reduceBody(body.getBytes());

        assertThat(new String(bytesReducedBody)).isEqualTo("{\"mime\": \"text/plain\", \"contenu\": \"77+977+977... (champ tronque, car trop long) ...ABHcmVlbnN\"}");
    }

}
