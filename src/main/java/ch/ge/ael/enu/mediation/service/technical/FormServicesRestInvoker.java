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

import static ch.ge.ael.enu.mediation.routes.http.Header.REMOTE_USER;
import static ch.ge.ael.enu.mediation.util.invocation.Precondition.checkNotBlank;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

/**
 * Service d'appel REST au backend FormServices.
 * Garantit que l'en-tete "remote_user" est incluse dans chaque requete REST.
 */
@Component
@Slf4j
public class FormServicesRestInvoker {

    public static final String ID_USAGER = "idUsager";

    @Value("${app.formservices.url}")
    private String formServicesUrl;

    @Resource
    private RestTemplate restTemplate;

    /**
     * Requete HEAD.
     */
    public <S, T> ResponseEntity<T> headEntity(String path, HttpEntity<S> requestEntity, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, null, requestEntity, HEAD, idUsager, typeReference);
    }

    /**
     * Requete GET.
     */
    public <T> T get(String path, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, null, null, GET, idUsager, typeReference).getBody();
    }

    /**
     * Requete POST.
     */
    public <S, T> T post(String path, S requestContents, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, requestContents, null, POST, idUsager, typeReference).getBody();
    }

    /**
     * Requete POST.
     */
    public <S, T> T postEntity(String path, HttpEntity<S> requestEntity, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, null, requestEntity, POST, idUsager, typeReference).getBody();
    }

    /**
     * Requete PUT.
     */
    public <S, T> T put(String path, S requestContents, String idUsager, ParameterizedTypeReference<T> typeReference) {
        return invoke(path, requestContents, null, PUT, idUsager, typeReference).getBody();
    }

    // passer ou bien "requestContents" ou bien "requestEntity" (voire aucun des deux)
    private <S, T> ResponseEntity<T> invoke(
            String path,
            S requestContents,
            HttpEntity<S> requestEntity,
            HttpMethod method,
            String idUsager,
            ParameterizedTypeReference<T> typeReference) {

        checkNotBlank(idUsager, ID_USAGER);

        HttpEntity<S> sentEntity;
        if (requestEntity == null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(REMOTE_USER, idUsager);
            sentEntity = new HttpEntity<>(requestContents, headers);
        } else {
            HttpHeaders writableHeaders = createWritableHeadersFrom(requestEntity.getHeaders());
            writableHeaders.add(REMOTE_USER, idUsager);
            sentEntity = new HttpEntity<>(requestEntity.getBody(), writableHeaders);
        }

        ResponseEntity<T> response = restTemplate.exchange(
                formServicesUrl + "/" + path,
                method,
                sentEntity,
                typeReference);

        HttpStatus status = response.getStatusCode();
        if (status.is2xxSuccessful()) {
            log.info("Appel REST a FormServices rend HTTP " + status);
        } else {
            log.warn("Appel REST a FormServices rend HTTP " + status);
        }

        return response;
    }

    /**
     * Rend une copie modifiable des headers passes en argument.
     */
    public static HttpHeaders createWritableHeadersFrom(HttpHeaders headers) {
        HttpHeaders writableHeaders = new HttpHeaders();
        headers.forEach((key, values) -> writableHeaders.add(key, values.get(0)));
        return writableHeaders;
    }

}
