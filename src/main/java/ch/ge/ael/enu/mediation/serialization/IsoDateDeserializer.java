package ch.ge.ael.enu.mediation.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Transforme en LocalDate une date reçue au format 2020-02-18".
 * @TODO CLASSE PEUT-ETRE INUTILE
 */
public class IsoDateDeserializer extends StdDeserializer<LocalDate> {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.FRENCH);

    public IsoDateDeserializer() {
        this(null);
    }

    public IsoDateDeserializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        return LocalDate.parse(parser.getText().toString(), FORMAT);
    }

}
