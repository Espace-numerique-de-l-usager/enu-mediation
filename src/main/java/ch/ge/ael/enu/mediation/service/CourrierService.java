package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.mediation.mapping.NewCourrierDocumentToJwayMapper;
import ch.ge.ael.enu.mediation.metier.model.NewCourrier;
import ch.ge.ael.enu.mediation.metier.model.NewCourrierDocument;
import ch.ge.ael.enu.mediation.metier.validation.NewCourrierValidator;
import ch.ge.ael.enu.mediation.routes.processing.NewCourrierSplitter;
import ch.ge.ael.enu.mediation.service.technical.DeserializationService;
import ch.ge.ael.enu.mediation.service.technical.FormServicesRestInvoker;
import ch.ge.ael.enu.mediation.service.technical.MessageLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service de creation d'un courrier, lie ou non a une demarche.
 * <br/>
 * Dans Jway il n'a pas d'entite de courrier, il n'y a que "n" entites de documents ; chaque document
 * possede les donnees du courrier, ce qui permet d'identifier les documents constituant un meme courrier.
 */
@Service
@Slf4j
public class CourrierService {

    /**
     * Types MIME (par ex. 'applicatiopn/pdf') de documents acceptes par la mediation.
     * Noter que si un type est ajoute a cette liste, le document n'est pas pour autant forcement accepte par
     * FormServices, car FormServivces a sa propre liste de types acceptes.
     */
    @Value("${app.document.mime-types}")
    private List<String> allowedMimeTypes;

    @Value("${app.file.name.sanitization-regex}")
    private String fileNameSanitizationRegex;

    @Resource
    private DeserializationService deserializationService;

    @Resource
    private MessageLogger messageLogger;

    @Resource
    private FormServicesRestInvoker formServices;

    private NewCourrierValidator validator;

    private NewCourrierSplitter splitter;

    private NewCourrierDocumentToJwayMapper mapper;

    @PostConstruct
    private void init() {
        validator = new NewCourrierValidator(allowedMimeTypes);
        splitter = new NewCourrierSplitter();
        mapper = new NewCourrierDocumentToJwayMapper(fileNameSanitizationRegex);
    }

    public void handle(Message message) {
        // deserialisation du message
        NewCourrier newCourrier = deserializationService.deserialize(message.getBody(), NewCourrier.class);

        // validation metier du message
        validator.validate(newCourrier);

        // ajout au courrier d'une clef technique. Cette clef sera affectee a chaque document constituant le
        // courrier et permettra donc de regrouper les documents du courrier
        newCourrier.setClef("Courrier-" + ZonedDateTime.now().toEpochSecond());

        // scission du courrier en "n" documents
        List<NewCourrierDocument> documents = splitter.splitCourrier(newCourrier);

        // pour chacun des "n" documents, creation du document dans FormServices
        documents.stream()
                .map(courrierDoc -> mapper.mapNewCourrierDocumentToBuilder(courrierDoc, newCourrier.getIdDemarcheSiMetier()))
                .forEach(entity -> {
                    messageLogger.logJsonSentToJway(entity.toString());
                    formServices.exchange("alpha/document", entity, newCourrier.getIdUsager());
                    log.info("Document cree");
                });
    }

}
