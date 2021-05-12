package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.validation.NewDemarcheValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Service de creation d'une demarche.
 */
@Service
@Slf4j
public class DemarcheService {

    @Resource
    private DeserializationService deserializationService;

    @Resource
    private FormServicesRestInvoker formServices;

    private NewDemarcheValidator validator = new NewDemarcheValidator();

    private NewDemarcheToJwayMapper newDemarcheToJwayMapper = new NewDemarcheToJwayMapper();

    public void handle(Message message) {
        // de-serialisation du message
        NewDemarche newDemarche = deserializationService.deserialize(message.getBody(), NewDemarche.class);
        log.info("newDemarche = {}", newDemarche);

        // validation metier du message
        validator.validate(newDemarche);

        // creation dans FormServices de la demarche a l'etat de brouillon
        File file = newDemarcheToJwayMapper.mapNewDemarcheToFile(newDemarche);
        Object response = formServices.post("alpha/file", file, Object.class);

        // passage dans FormServices a l'etat "deposee" (si pertinent)

        // passage dans FormServices a l'etat "en traitememt" (si pertinent)
    }

    private void createDemarcheBrouillon(NewDemarche newDemarche) {

    }

}
