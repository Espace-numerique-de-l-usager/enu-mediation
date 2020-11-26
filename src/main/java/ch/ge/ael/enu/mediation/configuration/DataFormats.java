package ch.ge.ael.enu.mediation.configuration;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Formats de marshalling / unmarshalling pour Camel.
 */
@Configuration
public class DataFormats {

    @Bean
    public JacksonDataFormat jwayFileDataFormat(ObjectMapper jackson) {
        JacksonDataFormat jwayFileDataFormat = new JacksonDataFormat();
        jwayFileDataFormat.setUnmarshalType(File.class);
        jwayFileDataFormat.setInclude("NON_NULL");
        jwayFileDataFormat.setObjectMapper(jackson);
        return jwayFileDataFormat;
    }

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

    @Bean
    public JacksonDataFormat metierStatusChangeDataFormat(ObjectMapper jackson) {
        JacksonDataFormat dataFormat = new JacksonDataFormat();
        dataFormat.setUnmarshalType(StatusChange.class);
        dataFormat.setInclude("NON_NULL");
        dataFormat.setObjectMapper(jackson);
        return dataFormat;
    }

}
