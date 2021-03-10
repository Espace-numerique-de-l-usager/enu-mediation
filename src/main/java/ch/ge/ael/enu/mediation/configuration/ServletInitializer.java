package ch.ge.ael.enu.mediation.configuration;

import ch.ge.ael.enu.mediation.MediationApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
public class ServletInitializer extends SpringBootServletInitializer {

    private final String tomcatHome = System.getProperty("catalina.home");

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        log.info("ServletInitializer -> startup with tomcatHome = " + tomcatHome);
        return builder.sources(MediationApplication.class)
                .properties("spring.config.name: application,enu-mediation")
                .properties("spring.config.location: " + tomcatHome + "/conf/,classpath:/");
    }
}
