package ch.ge.ael.enu.mediation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "file:${catalina.base}/conf/enu-mediation.properties", ignoreResourceNotFound = true)  // uniquement pour execution sous Tomcat
//@PropertySource(value = "classpath:enu-mediation-default.properties")  // pour execution locale et pour execution sous Tomcat
public class MediationApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MediationApplication.class, args);
    }

}
