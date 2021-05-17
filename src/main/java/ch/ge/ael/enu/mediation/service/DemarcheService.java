package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.validation.NewDemarcheValidator;
import ch.ge.ael.enu.mediation.routes.processing.NewDemarcheToBrouillonReducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_TRAITEMENT;

/**
 * Service de creation d'une demarche, a l'etat "brouillon", "deposee" ou "en traitement".
 */
@Service
@Slf4j
public class DemarcheService {

    @Resource
    private DeserializationService deserializationService;

    @Resource
    private FormServicesRestInvoker formServices;

    private NewDemarcheValidator validator = new NewDemarcheValidator();

    private NewDemarcheToBrouillonReducer reducer = new NewDemarcheToBrouillonReducer();

    private NewDemarcheToJwayMapper newDemarcheToJwayMapper = new NewDemarcheToJwayMapper();

    public void handle(Message message) {
        // deserialisation du message
        NewDemarche newDemarche = deserializationService.deserialize(message.getBody(), NewDemarche.class);
        log.info("newDemarche = {}", newDemarche);

        // validation metier du message
        validator.validate(newDemarche);
        DemarcheStatus status = DemarcheStatus.valueOf(newDemarche.getEtat());

        // creation dans FormServices de la demarche a l'etat de brouillon
        NewDemarche newDemarcheBrouillon = reducer.reduce(newDemarche);
        File file = newDemarcheToJwayMapper.mapNewDemarcheToFile(newDemarcheBrouillon);
        File createdFile = formServices.post("alpha/file", file, newDemarche.getIdUsager(), File.class);
        log.info("Demarche creee, uuid = [{}]", createdFile.getUuid());

        // passage dans FormServices a l'etat "deposee" (si pertinent)
        if (status == DEPOSEE || status == EN_TRAITEMENT) {
            log.warn("A FAIRE : passage a l'etat DEPOSEE");
        }

        // passage dans FormServices a l'etat "en traitememt" (si pertinent)
        if (status == EN_TRAITEMENT) {
            log.warn("A FAIRE : passage a l'etat EN TRAITEMENT");
        }
    }

}
