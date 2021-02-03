package ch.ge.ael.enu.mediation.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Transforme en LocalDateTime une date reçue de FormServices, comme "2020-11-25T15:42:05.445+0000" ou
 * "2020-11-25T15:42:05.445+00:00".
 */
public class JwayDateDeserializer extends StdDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMAT_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+0000", Locale.FRENCH);

    private static final DateTimeFormatter FORMAT_2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00", Locale.FRENCH);

    public JwayDateDeserializer() {
        this(null);
    }

    public JwayDateDeserializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        // on essaie avec un format, puis avec l'autre
        try {
          return LocalDateTime.parse(parser.getText(), FORMAT_1);
        } catch (Exception e) {
          return LocalDateTime.parse(parser.getText(), FORMAT_2);
        }
    }

}
