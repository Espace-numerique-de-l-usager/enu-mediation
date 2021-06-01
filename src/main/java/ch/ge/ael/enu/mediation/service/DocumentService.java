package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.mediation.business.domain.NewCourrier;
import ch.ge.ael.enu.mediation.business.domain.NewCourrierDocument;
import ch.ge.ael.enu.mediation.business.domain.NewDocument;
import ch.ge.ael.enu.mediation.business.validation.NewCourrierValidator;
import ch.ge.ael.enu.mediation.business.validation.NewDocumentValidator;
import ch.ge.ael.enu.mediation.jway.model.Document;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewCourrierDocumentToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.NewDocumentToJwayMapper;
import ch.ge.ael.enu.mediation.routes.processing.NewCourrierSplitter;
import ch.ge.ael.enu.mediation.service.technical.DeserializationService;
import ch.ge.ael.enu.mediation.service.technical.FormServicesRestInvoker;
import ch.ge.ael.enu.mediation.service.technical.MessageLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.ZonedDateTime;
import java.util.List;

import static ch.ge.ael.enu.mediation.routes.http.Header.X_CSRF_TOKEN;
import static java.lang.String.format;

/**
 * Service de gestion des documents :
 * <ul>
 *     <li>ajout d'un document a une demarche</li>
 *     <li>creation d'un courrier, lie ou non a une demarche.</li>
 * </ul>
 * Note sur les courriers :
 * dans Jway il n'a pas d'entite de courrier, il n'y a que "n" entites de documents ; chaque document
 * possede les donnees du courrier, ce qui permet d'identifier les documents constituant un meme courrier.
 */
@Service
@Slf4j
public class DocumentService {

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
    private DemarcheService demarcheService;

    @Resource
    private MessageLogger messageLogger;

    @Resource
    private FormServicesRestInvoker formServices;

    private NewDocumentValidator newDocumentValidator;

    private NewCourrierValidator newCourrierValidator;

    private NewCourrierSplitter splitter;

    private NewDocumentToJwayMapper newDocumentToJwayMapper;

    private NewCourrierDocumentToJwayMapper newCourrierDocumentToJwayMapper;

    @PostConstruct
    private void init() {
        newDocumentValidator = new NewDocumentValidator(allowedMimeTypes);
        newCourrierValidator = new NewCourrierValidator(allowedMimeTypes);
        splitter = new NewCourrierSplitter();
        newDocumentToJwayMapper = new NewDocumentToJwayMapper(fileNameSanitizationRegex);
        newCourrierDocumentToJwayMapper = new NewCourrierDocumentToJwayMapper(fileNameSanitizationRegex);
    }

    public void handleNewDocument(Message message) {
        // deserialisation du message
        NewDocument newDocument = deserializationService.deserialize(message.getBody(), NewDocument.class);

        // validation metier du message
        newDocumentValidator.validate(newDocument);
        String idDemarcheSiMetier = newDocument.getIdDemarcheSiMetier();
        String idUsager = newDocument.getIdUsager();

        // recuperation dans FormServices de l'uuid de la demarche
        File demarche = demarcheService.getDemarche(idDemarcheSiMetier, idUsager);
        String demarcheUuid = demarche.getUuid().toString();
        log.info("UUID demarche = [{}]", demarcheUuid);

        // requete HEAD pour recuperer un jeton CSRF. Sans cette phase, on obtient une erreur 403 plus bas
        String path = format("document/ds/%s/attachment", demarcheUuid);
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_CSRF_TOKEN, "fetch");
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);
        ParameterizedTypeReference<String> typeReference = new ParameterizedTypeReference<String>() {};
//        messageLogger.logJsonSent(path, entity.toString());
        ResponseEntity<String> response = formServices.headEntity(path, entity, idUsager, typeReference);
        String token = response.getHeaders().get(X_CSRF_TOKEN).get(0);
        log.info("Jeton CSRF obtenu = [{}]", token);

        // requete proprement dite d'envoi a FormServices
        String path2 = format("document/ds/%s/attachment", demarcheUuid);
        HttpEntity entity2 = newDocumentToJwayMapper.map(newDocument, demarcheUuid);
        HttpHeaders writableHeaders = FormServicesRestInvoker.createWritableHeadersFrom(entity2.getHeaders());
        writableHeaders.add(X_CSRF_TOKEN, token);
        entity2 = new HttpEntity<>(entity2.getBody(), writableHeaders);
        ParameterizedTypeReference<Document> typeReference2 = new ParameterizedTypeReference<Document>() {};
//        messageLogger.logJsonSent(path2, entity2.toString());
        formServices.postEntity(path2, entity2, idUsager, typeReference2);
        log.info("Document cree");
    }

    public void handleNewCourrier(Message message) {
        // deserialisation du message
        NewCourrier newCourrier = deserializationService.deserialize(message.getBody(), NewCourrier.class);

        // validation metier du message
        newCourrierValidator.validate(newCourrier);

        // ajout au courrier d'une clef technique. Cette clef sera affectee a chaque document constituant le
        // courrier et permettra donc de regrouper les documents du courrier
        newCourrier.setClef("Courrier-" + ZonedDateTime.now().toEpochSecond());

        // scission du courrier en "n" documents
        List<NewCourrierDocument> documents = splitter.splitCourrier(newCourrier);

        // pour chacun des "n" documents, creation du document dans FormServices
        documents.stream()
                .map(courrierDoc -> newCourrierDocumentToJwayMapper.map(courrierDoc, newCourrier.getIdDemarcheSiMetier()))
                .forEach(entity -> {
                    String path = "alpha/document";
//                    messageLogger.logJsonSent(path, entity.toString());
                    ParameterizedTypeReference<Document> typeReference = new ParameterizedTypeReference<Document>() {};
                    formServices.postEntity(path, entity, newCourrier.getIdUsager(), typeReference);
                    log.info("Document de courrier cree");
                });
    }

}
