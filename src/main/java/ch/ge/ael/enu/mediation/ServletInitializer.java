package ch.ge.ael.enu.mediation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * This class has to be in Spring context so the Spring Boot app starts in a Tomcat / WAR
 */
@Configuration
@Slf4j
public class ServletInitializer extends SpringBootServletInitializer {
    private final String tomcatHome = System.getProperty("catalina.home");

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        log.info("ServletInitializer -> startup with tomcatHome = " + tomcatHome);
        return builder.sources(MediationApplication.class)
                .properties("spring.config.name:enu-mediation")
                .properties("spring.config.location:" + tomcatHome + "/conf/");
    }
}
