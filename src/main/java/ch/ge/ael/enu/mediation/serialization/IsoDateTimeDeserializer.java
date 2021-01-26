package ch.ge.ael.enu.mediation.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IsoDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    public IsoDateTimeDeserializer() {
        this(null);
    }

    public IsoDateTimeDeserializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        return LocalDateTime.parse(parser.getText(), FORMAT);
    }

}
