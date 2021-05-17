package ch.ge.ael.enu.mediation.service.technical;

import ch.ge.ael.enu.mediation.exception.IllegalMessageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class DeserializationService {

    @Resource
    private ObjectMapper mapper;

    public <T> T deserialize(byte[] content, Class<T> clazz) {
        T object;
        try {
            object = mapper.readValue(content, clazz);
        } catch(Exception e) {
            log.info("Erreur lors de la deserialisation : {}", e.getMessage());
            throw new IllegalMessageException("Erreur lors de la deserialisation du message JSON : " + e.getMessage());
        }
        return object;
    }

}
