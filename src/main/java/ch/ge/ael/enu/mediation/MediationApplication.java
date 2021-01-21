package ch.ge.ael.enu.mediation;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
//@PropertySource("classpath:.enu/mediation.properties")
//@PropertySource("file:${application.properties}")
public class MediationApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediationApplication.class);

    public static void main(String[] args) {
//        SpringApplication.run(MediationApplication.class, args);
        configureApplication(new SpringApplicationBuilder()).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return super.configure(builder);
        return configureApplication(builder);
    }

    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        final String FILE_NAME_PROPERTY = "mediation.properties";
        String fileName = System.getProperty(FILE_NAME_PROPERTY);
        Properties properties = new Properties();
        if (fileName == null) {
            LOGGER.info("Propriete [{}] pas definie (normal en execution locale, anormal en execution Tomcat)", FILE_NAME_PROPERTY);
        } else {
            LOGGER.info("Chargement des proprietes supplementaires definies dans le fichier [{}]", fileName);
            try (InputStream input = new FileInputStream(fileName)) {
                properties.load(input);
            } catch (IOException e) {
                LOGGER.error("Erreur : ", e);
            }
            LOGGER.info("Proprietes lues : {}", properties);
        }

        return builder
                .properties(properties)
                .sources(MediationApplication.class);
    }


}
