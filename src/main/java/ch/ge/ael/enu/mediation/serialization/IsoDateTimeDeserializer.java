package ch.ge.ael.enu.mediation.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Transforme en LocalDateTime une date reçue de FormServices, comme "2020-11-25T15:42:05.445+0000".
 */
public class IsoDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRENCH);

    public IsoDateTimeDeserializer() {
        this(null);
    }

    public IsoDateTimeDeserializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        return LocalDateTime.parse(parser.getText().toString(), FORMAT);
    }

}
