package ch.ge.ael.enu.mediation.service.technical;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import static ch.ge.ael.enu.mediation.util.invocation.Precondition.checkNotBlank;

/**
 * Service d'appel REST au backend FormServices.
 */
@Component
@Slf4j
public class FormServicesRestInvoker {

    public static final String REMOTE_USER = "remote_user";

    @Value("${app.formservices.url}")
    private String formServicesUrl;

    @Resource
    private RestTemplate restTemplate;

    public <T> T post(String path, Object contents, String idUsager, Class<T> clazz) {
        checkNotBlank(idUsager, "idUsager");

        HttpHeaders headers = new HttpHeaders();
        headers.add(REMOTE_USER, idUsager);

        HttpEntity entity = new HttpEntity(contents, headers);

        return restTemplate.postForObject(formServicesUrl + "/" + path, entity, clazz);
    }

    public <T> ResponseEntity<T> exchange(String path, HttpEntity<T> entity, String idUsager) {
        checkNotBlank(idUsager, "idUsager");

        ParameterizedTypeReference<T> typeReference = new ParameterizedTypeReference<T>() {};

        return restTemplate.exchange(formServicesUrl + "/" + path, HttpMethod.POST, entity, typeReference);
    }

}
