package ch.ge.ael.enu.mediation.configuration;

import ch.ge.ael.enu.mediation.serialization.MillisOrLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Configuration
public class ApplicationConfiguration {

    /**
     * Config générale du marshaller Jackson pour le contexte Spring
     */
    @Bean(name = "json-jackson")
    public ObjectMapper jackson() {
        ObjectMapper jackson = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new MillisOrLocalDateTimeDeserializer());
        jackson.registerModule(javaTimeModule);
        jackson.setDateFormat(DateFormat.getDateInstance());
        jackson.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jackson.enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);
        jackson.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jackson.setTimeZone(TimeZone.getDefault());
        return jackson;
    }

    @Value("${app.formservices.ssl.trust-store.resource}")
    private String trustStorePath;

    @Value("${app.formservices.ssl.trust-store.password}")
    private String trustStorePassword;

    @Bean
    public RestTemplate restTemplate() throws Exception {
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(
                        ResourceUtils.getFile(trustStorePath), trustStorePassword.toCharArray())
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(client);

        return new RestTemplate(requestFactory);
    }

}
