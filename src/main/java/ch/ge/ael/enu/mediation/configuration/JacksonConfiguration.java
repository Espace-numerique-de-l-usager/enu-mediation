package ch.ge.ael.enu.mediation.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Config générale du marshaller Jackson pour le contexte Spring
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper jackson() {
        ObjectMapper jackson = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new MillisOrLocalDateTimeDeserializer());
        jackson.registerModule(javaTimeModule);
        jackson.setDateFormat(DateFormat.getDateInstance());
        jackson.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jackson.enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);
        jackson.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // sans effet ; le champ "id" reste serialise'
        jackson.setTimeZone(TimeZone.getDefault());
        return jackson;
    }

}
