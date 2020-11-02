package ch.ge.ael.enu.mediation.configuration;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Formats de marshalling / unmarshalling pour Camel
 */
@Configuration
public class DataFormats {

    @Bean
    public JacksonDataFormat jwayFileListDataFormat(ObjectMapper jackson) {
        JacksonDataFormat jwayFileDataFormat = new ListJacksonDataFormat();
        jwayFileDataFormat.setUnmarshalType(File.class);
        jwayFileDataFormat.setInclude("NON_NULL");
        jwayFileDataFormat.setObjectMapper(jackson);
        return jwayFileDataFormat;
    }

    @Bean
    public JacksonDataFormat metierNewDemarcheDataFormat(ObjectMapper jackson) {
        JacksonDataFormat jwayFileDataFormat = new JacksonDataFormat();
        jwayFileDataFormat.setUnmarshalType(NewDemarche.class);
        jwayFileDataFormat.setInclude("NON_NULL");
        jwayFileDataFormat.setObjectMapper(jackson);
        return jwayFileDataFormat;
    }
}
