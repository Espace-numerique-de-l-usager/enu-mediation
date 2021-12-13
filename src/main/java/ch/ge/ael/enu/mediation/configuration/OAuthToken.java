package ch.ge.ael.enu.mediation.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.impl.CredentialsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * Used to print the OAuth token for this demo. No need for it in a real application.
 */
@Component
@RequiredArgsConstructor
public class OAuthToken {
    private final CredentialsProvider credentialsProvider;
    private final ObjectMapper objectMapper;

    public static class Token {
        public final String value;
        public final long secondsRemaining;

        public Token(String value, long secondsRemaining) {
            this.value = value;
            this.secondsRemaining = secondsRemaining;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "value='" + value + '\'' +
                    ", secondsRemaining=" + secondsRemaining +
                    '}';
        }
    }

    public Token getLatestToken() throws Exception {
        String value = toPrettyJsonString(parseToken(credentialsProvider.getPassword()));
        return new Token(value, credentialsProvider.getTimeBeforeExpiration().getSeconds());
    }

    private Map<String, ?> parseToken(String base64Token) throws IOException {
        String token = base64Token.split("\\.")[1];
        return objectMapper.readValue(Base64.getDecoder().decode(token), new TypeReference<Map<String, ?>>() {
        });
    }

    private String toPrettyJsonString(Object object) throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
