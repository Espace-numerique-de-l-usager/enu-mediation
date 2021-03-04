package ch.ge.ael.enu.mediation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:enu-mediation-default.properties")  // pour execution locale et pour execution sous Tomcat
@PropertySource(value = "file:***REMOVED***/conf/enu-mediation.properties")  // uniquement pour execution sous Tomcat
public class MediationApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MediationApplication.class, args);
    }

}
