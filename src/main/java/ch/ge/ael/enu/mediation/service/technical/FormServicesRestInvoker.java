package ch.ge.ael.enu.mediation.service.technical;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import static ch.ge.ael.enu.mediation.util.invocation.Precondition.checkNotBlank;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

/**
 * Service d'appel REST au backend FormServices.
 */
@Component
@Slf4j
public class FormServicesRestInvoker {

    public static final String REMOTE_USER = "remote_user";

    public static final String ID_USAGER = "idUsager";

    @Value("${app.formservices.url}")
    private String formServicesUrl;

    @Resource
    private RestTemplate restTemplate;

    public <T> T get(String path, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, null, null, GET, idUsager, typeReference);
    }

    public <T> T post(String path, Object contents, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, contents, null, POST, idUsager, typeReference);
    }

    public <T> T postEntity(String path, HttpEntity entity, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, null, entity, POST, idUsager, typeReference);
    }

    public <T> T put(String path, Object contents, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, contents, null, PUT, idUsager, typeReference);
    }

    private <T> T invoke(
            String path,
            Object contents,
            HttpEntity entity,
            HttpMethod method,
            String idUsager,
            ParameterizedTypeReference<T> typeReference) {

        checkNotBlank(idUsager, ID_USAGER);

        HttpEntity sentEntity;
        if (entity == null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(REMOTE_USER, idUsager);
            sentEntity = new HttpEntity(contents, headers);
        } else {
            HttpHeaders writableHeaders = new HttpHeaders();
            // copie de headers vers un headers modifiable
            entity.getHeaders().entrySet()
                    .forEach(e -> writableHeaders.add(e.getKey(), e.getValue().get(0)));
            writableHeaders.add(REMOTE_USER, idUsager);
            sentEntity = new HttpEntity<>(entity.getBody(), writableHeaders);
        }

        ResponseEntity<T> response = restTemplate.exchange(
                formServicesUrl + "/" + path,
                method,
                sentEntity,
                typeReference);

        HttpStatus status = response.getStatusCode();
        if (status.is2xxSuccessful()) {
            log.info("Appel REST a Jway rend HTTP " + status);
        } else {
            log.warn("Appel REST a Jway rend HTTP " + status);
        }

        return response.getBody();
    }

}
