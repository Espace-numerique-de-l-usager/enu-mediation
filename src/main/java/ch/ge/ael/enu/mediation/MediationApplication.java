package ch.ge.ael.enu.mediation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@Slf4j
public class MediationApplication extends SpringBootServletInitializer {

    private final String tomcatHome = System.getProperty("catalina.home");

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application,enu-mediation");
        SpringApplication.run(MediationApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        log.info("ServletInitializer -> startup with tomcatHome = " + tomcatHome);
        return builder.sources(MediationApplication.class)
                .properties("spring.config.name: application,enu-mediation")
                .properties("spring.config.location: " + tomcatHome + "/conf/,classpath:/");
    }

}
