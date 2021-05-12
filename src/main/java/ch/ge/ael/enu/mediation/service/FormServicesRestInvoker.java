package ch.ge.ael.enu.mediation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * Service d'appel REST au backend FormServices.
 */
@Component
@Slf4j
public class FormServicesRestInvoker {

    @Value("${app.formservices.url}")
    private String formServicesUrl;

    @Resource
    private RestTemplate restTemplate;

    public <T> T post(String path, Object request, Class<T> clazz) {
        return restTemplate.postForObject(formServicesUrl + "/" + path, request, clazz);
    }

}
