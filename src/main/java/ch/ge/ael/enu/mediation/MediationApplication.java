package ch.ge.ael.enu.mediation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MediationApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application,enu-mediation");
        SpringApplication.run(MediationApplication.class, args);
    }
}
