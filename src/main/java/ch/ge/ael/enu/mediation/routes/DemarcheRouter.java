package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.error.MessageFailureEnricher;
import ch.ge.ael.enu.mediation.error.UnroutableMessageProcessor;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewCourrierDocumentToJwayMapperProcessor;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToStatusChangeMapper;
import ch.ge.ael.enu.mediation.mapping.NewDocumentToJwayMapperProcessor;
import ch.ge.ael.enu.mediation.mapping.NewSuggestionToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep1Mapper;
import ch.ge.ael.enu.mediation.mapping.StatusChangeToJwayStep2Mapper;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.NewCourrier;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import ch.ge.ael.enu.mediation.metier.model.NewSuggestion;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;
import ch.ge.ael.enu.mediation.metier.validation.NewCourrierValidator;
import ch.ge.ael.enu.mediation.metier.validation.NewDemarcheValidator;
import ch.ge.ael.enu.mediation.metier.validation.NewDocumentValidator;
import ch.ge.ael.enu.mediation.metier.validation.NewSuggestionValidator;
import ch.ge.ael.enu.mediation.metier.validation.StatusChangeValidator;
import ch.ge.ael.enu.mediation.routes.http.Header;
import ch.ge.ael.enu.mediation.routes.processing.NewCourrierKeySetter;
import ch.ge.ael.enu.mediation.routes.processing.NewCourrierSplitter;
import ch.ge.ael.enu.mediation.routes.processing.NewDemarcheToBrouillonReducer;
import ch.ge.ael.enu.mediation.routes.processing.OldExchangeStrategy;
import ch.ge.ael.enu.mediation.routes.processing.PropertyPropagationStrategy;
import ch.ge.ael.enu.mediation.util.logging.BodyReducer;
import ch.ge.ael.enu.mediation.util.logging.MultipartJwayBodyReducer;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.ge.ael.enu.mediation.mapping.NewDocumentToJwayMapperProcessor.MULTIPART_BOUNDARY;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_TRAITEMENT;
import static ch.ge.ael.enu.mediation.routes.http.Header.RABBITMQ_CONTENT_TYPE;
import static ch.ge.ael.enu.mediation.routes.http.MediaType.NEW_COURRIER;
import static ch.ge.ael.enu.mediation.routes.http.MediaType.NEW_DEMARCHE;
import static ch.ge.ael.enu.mediation.routes.http.MediaType.NEW_DOCUMENT;
import static ch.ge.ael.enu.mediation.routes.http.MediaType.NEW_SUGGESTION;
import static ch.ge.ael.enu.mediation.routes.http.MediaType.STATUS_CHANGE;

/**
 * Ceci est la classe principale de toute l'application.
 * Elle definit la consommation (et parfois la production) des messages RabbitMQ et
 * leur transmission vers FormServices.
 */
@Component
@RequiredArgsConstructor
public class DemarcheRouter extends RouteBuilder {

    private final CamelContext camelContext;

    @Value("${app.formservices.url}")
    private String formServicesUrl;

    /**
     * Taille (en bytes Base 64) des fichiers au-dela de laquelle le contenu des fichiers n'est plus trace dans
     * son integralite dans la console et dans les fichiers de traces.
     */
    @Value("${app.logging.max-file-content-size}")
    private int maxFileContentSize;

    /**
     * Types MIME (par ex. 'applicatiopn/pdf') de documents acceptes par la mediation.
     * Noter que si un type est ajoute a cette liste, le document n'est pas pour autant forcement accepte par
     * FormServices qui a sa propre liste de types acceptes.
     */
    @Value("${app.document.mime-types}")
    private List<String> allowedMimeTypes;

    @Autowired
    private NewDocumentToJwayMapperProcessor newDocumentToJwayMapper;

    @Autowired
    private NewCourrierDocumentToJwayMapperProcessor newCourrierDocumentToJwayMapper;

    static final String MAIN_QUEUE = "rabbitmq:"
            + "simetier1-to-enu-main?"
            + "queue=simetier1-to-enu-main-q"
            + "&deadLetterExchange=enu-to-simetier1-reply"
            + "&deadLetterQueue=enu-to-simetier1-reply-q"
            + "&deadLetterRoutingKey=enu-to-simetier1-reply-q"
            + "&autoDelete=false";

    static final String DEAD_LETTER_QUEUE = "rabbitmq:"
            + "enu-to-simetier1-reply?"
            + "queue=enu-to-simetier1-reply-q";

    static final String INTERNAL_ERROR_QUEUE = "rabbitmq:"
            + "enu-internal-error?"
            + "queue=enu-internal-error-q";

    private final Predicate isNewDemarche = header(RABBITMQ_CONTENT_TYPE).isEqualTo(NEW_DEMARCHE);

    private final Predicate isNewSuggestion = header(RABBITMQ_CONTENT_TYPE).isEqualTo(NEW_SUGGESTION);

    private final Predicate isStatusChange = header(RABBITMQ_CONTENT_TYPE).isEqualTo(STATUS_CHANGE);

    private final Predicate isNewDocument = header(RABBITMQ_CONTENT_TYPE).isEqualTo(NEW_DOCUMENT);

    private final Predicate isNewCourrier = header(RABBITMQ_CONTENT_TYPE).isEqualTo(NEW_COURRIER);

    private final Predicate isNewDemarcheDeposee = jsonpath("$[?(@.etat=='" + DEPOSEE + "')]");

    private final Predicate isNewDemarcheEnTraitement = jsonpath("$[?(@.etat=='" + EN_TRAITEMENT + "')]");

    public static final String UUID = "uuid";                    // a mettre dans une classe HeaderName ?

    public static final String ID_PRESTATION = "idPrestation";   // a mettre dans une classe HeaderName ?

    private static final String CSRF_TOKEN = "csrf-token";

    private static final String X_CSRF_TOKEN = "X-CSRF-Token";

    private static final String CODE_REPONSE = "Reponse : HTTP ${header.CamelHttpResponseCode}";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String REMOTE_USER = "remote_user";

    /**
     * Definition des routes.
     */
    @Override
    public void configure() {
        camelContext.setStreamCaching(true);
        restConfiguration()
                .host(formServicesUrl)
                .producerComponent("http");

        // attrape-tout
//        errorHandler(deadLetterChannel("rabbitmq:" + INTERNAL_ERROR_QUEUE).useOriginalMessage());

        onException(ValidationException.class)
                .log("Erreur de validation")
                .useOriginalMessage()
                .process(new MessageFailureEnricher())
                .log("body dans onException : ${body}")    // TODO: tronquer
                .log("headers dans onException : ${headers}")
                .log("Envoi a RabbitMQ du message d'erreur")
                .to(DEAD_LETTER_QUEUE);

        onException(Exception.class)
                .log("Erreur de traitement")
//                .useOriginalMessage()
                .process(new MessageFailureEnricher())
                .log("body dans onException : ${body}")    // TODO: tronquer
                .log("headers dans onException : ${headers}")
                .log("Envoi a RabbitMQ du message d'erreur")
                .to(INTERNAL_ERROR_QUEUE);

        // routage principal
        from(MAIN_QUEUE).id("route-principale")
                .log("********************************")
                .log("*** Message recu de RabbitMQ ***")
                .log("********************************")
                .enrich("direct:log-message", new OldExchangeStrategy())
                .choice()
                    .when(isNewDemarche)
                        .to("direct:nouvelle-demarche")
                    .when(isNewSuggestion)
                        .to("direct:nouvelle-suggestion")
                    .when(isStatusChange)
                        .to("direct:changement-etat-demarche")
                    .when(isNewDocument)
                        .to("direct:nouveau-document")
                    .when(isNewCourrier)
                        .to("direct:nouveau-courrier")
                    .otherwise()
                        .log("Message au contenu non reconnu")
                        .process(new UnroutableMessageProcessor());

        // trace du message entrant
        from("direct:log-message").id("log-message")
                .bean(new BodyReducer(maxFileContentSize))
                .to("log:INFO?showHeaders=true");

        // nouvelle demarche (en "brouillon" ou "deposee" ou "en traitement")
        from("direct:nouvelle-demarche").id("nouvelle-demarche")
                // prevoir un ExceptionHandler pour com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
                .log("* ROUTE nouvelle-demarche")
                .enrich("direct:nouvelle-demarche-brouillon", new OldExchangeStrategy())
                .setProperty("newDemarche", body())
                .choice()
                    .when(isNewDemarcheDeposee).id("nouvelle-demarche-deposee")
                        .log("Passage a l'etat DEPOSEE")
                        .to("log:input")
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(DEPOSEE))
                        .marshal(pojoToJson())
                        .to("direct:changement-etat-demarche")
                    .when(isNewDemarcheEnTraitement).id("nouvelle-demarche-en-traitement")
                        .log("Passage a l'etat DEPOSEE (avant le passage a l'etat EN_TRAITEMENT)")
                        .to("log:input")
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(DEPOSEE))
                        .marshal(pojoToJson())
                        .to("direct:changement-etat-demarche")
                        .log("Passage a l'etat EN_TRAITEMENT")
                        .setBody(exchangeProperty("newDemarche"))
                        .to("log:input")
                        .unmarshal(jsonToPojo(NewDemarche.class))
                        .bean(new NewDemarcheToStatusChangeMapper(EN_TRAITEMENT))
                        .marshal(pojoToJson())
                        .to("direct:changement-etat-demarche")
                    .otherwise()
                        .log("On en reste a l'etat BROUILLON");

        // nouvelle demarche (creation a l'etat brouillon)
        from("direct:nouvelle-demarche-brouillon").id("nouvelle-demarche-brouillon")
                .log("* ROUTE nouvelle-demarche-brouillon")
                .unmarshal(jsonToPojo(NewDemarche.class))
                .bean(NewDemarcheValidator.class)
                .to("log:input")
                .bean(NewDemarcheToBrouillonReducer.class)
                .setHeader(CONTENT_TYPE, simple("application/json"))
                .setHeader(REMOTE_USER, simple("${body.idUsager}", String.class))
                .bean(NewDemarcheToJwayMapper.class)
                .marshal(pojoToJson())
                .log(traceJsonToJway())
                .to("rest:post:alpha/file")
                .log(CODE_REPONSE)
                .unmarshal(jsonToPojo(File.class))
                .log("Demarche creee, uuid = ${body.uuid}");

        // nouvelle suggestion de demarche
        from("direct:nouvelle-suggestion").id("nouvelle-suggestion")
                .log("* ROUTE nouvelle-suggestion")
                .unmarshal(jsonToPojo(NewSuggestion.class))
                .bean(NewSuggestionValidator.class)
                .to("log:input")
                .setHeader(CONTENT_TYPE, simple("application/json"))
                .setHeader(REMOTE_USER, simple("${body.idUsager}", String.class))
                .bean(NewSuggestionToJwayMapper.class)
                .marshal(pojoToJson())
                .log(traceJsonToJway())
                .to("rest:post:alpha/file")
                .log(CODE_REPONSE)
                .unmarshal(jsonToPojo(File.class))
                .log("Suggestion creee, uuid = ${body.uuid}");

        // changement d'etat d'une demarche
        from("direct:changement-etat-demarche").id("changement-etat-demarche")
                .log("* ROUTE changement-etat-demarche")
                .unmarshal(jsonToPojo(StatusChange.class))
                .bean(StatusChangeValidator.class)
                .to("log:input")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:changement-etat-demarche-phase-1", new PropertyPropagationStrategy(UUID))
                .enrich("direct:changement-etat-demarche-phase-2", new PropertyPropagationStrategy(UUID))
                .to("direct:changement-etat-demarche-phase-3");

        // changement d'etat d'une demarche, phase 1 : recuperation de son uuid
        from("direct:changement-etat-demarche-phase-1").id("changement-etat-demarche-phase-1")
                .log("* ROUTE changement-etat-demarche-phase-1")
                .to("log:input")
                .setProperty("idDemarcheSiMetier", simple("${body.idDemarcheSiMetier}", String.class))
                .setHeader("name", exchangeProperty("idDemarcheSiMetier"))
                .setHeader(CONTENT_TYPE, simple("application/json"))
                .setHeader(REMOTE_USER, exchangeProperty("remoteUser"))
                .to("rest:get:file/mine?name={name}&max=1&order=id&reverse=true")  // ajouter &application.id={idPrestation}
                .log(CODE_REPONSE)
                .log("JSON obtenu de Jway = ${body}")
                .unmarshal(jsonToList(File.class))   // en faire une propriété
                .setProperty(UUID, simple("${body[0].uuid}", String.class))
                .log("uuid = ${body[0].uuid}");

        // changement d'etat d'une demarche, phase 2 : changement du step
        from("direct:changement-etat-demarche-phase-2").id("changement-etat-demarche-phase-2")
                .log("* ROUTE changement-etat-demarche-phase-2")
                .to("log:input")
                .setHeader(REMOTE_USER, exchangeProperty("remoteUser"))
                .setHeader("uuid", exchangeProperty(UUID))
                .bean(StatusChangeToJwayStep1Mapper.class)
                .marshal(pojoToJson())
                .log(traceJsonToJway())
                .to("rest:post:alpha/file/{uuid}/step")
                .log(CODE_REPONSE);
                // valider ici 204

        // changement d'etat d'une demarche, phase 3 : changement du workflowStatus
        from("direct:changement-etat-demarche-phase-3").id("changement-etat-demarche-phase-3")
                .log("* ROUTE changement-etat-demarche-phase-3")
                .to("log:input")
                .setHeader(REMOTE_USER, exchangeProperty("remoteUser"))
                .setHeader(UUID, exchangeProperty(UUID))
                .bean(StatusChangeToJwayStep2Mapper.class)
                .marshal(pojoToJson())
                .log(traceJsonToJway())
                .to("rest:put:alpha/file/{uuid}")
                .log("Changement d'etat OK")
                .log(CODE_REPONSE);
                // valider ici 204

        // ajout d'un document a une demarche
        from("direct:nouveau-document").id("nouveau-document")
                .log("* ROUTE nouveau-document")
                .unmarshal(jsonToPojo(NewDocument.class))
                .bean(new NewDocumentValidator(allowedMimeTypes))
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .enrich("direct:nouveau-document-phase-1", new PropertyPropagationStrategy(UUID))
                .enrich("direct:nouveau-document-phase-2", new PropertyPropagationStrategy(UUID, CSRF_TOKEN))
                .to("direct:nouveau-document-phase-3");

        // ajout d'un document a une demarche, phase 1 : recuperation de l'uuid de la demarche
        from("direct:nouveau-document-phase-1").id("nouveau-document-phase-1")
                .log("* ROUTE nouveau-document-phase-1")
                .setProperty("idDemarcheSiMetier", simple("${body.idDemarcheSiMetier}", String.class))
                .setHeader("name", exchangeProperty("idDemarcheSiMetier"))
                .setHeader(CONTENT_TYPE, simple("application/json"))
                .setHeader(REMOTE_USER, exchangeProperty("remoteUser"))
                .to("rest:get:file/mine?name={name}&max=1&order=id&reverse=true")  // ajouter &application.id={idPrestation}
                .log(CODE_REPONSE)
                .log("JSON obtenu de Jway = ${body}")
                .unmarshal(jsonToList(File.class))   // en faire une propriété
                .setProperty(UUID, simple("${body[0].uuid}", String.class))
                .log("uuid = ${body[0].uuid}");

        // ajout d'un document a une demarche, phase 2 : requete HEAD pour recuperer un jeton CSRF.
        // Sans cette phase, on obtient une erreur 403 dans la phase suivante 3
        from("direct:nouveau-document-phase-2").id("nouveau-document-phase-2")
                .log("* ROUTE nouveau-document-phase-2")
                .setHeader(X_CSRF_TOKEN, simple("fetch"))
                .setHeader(REMOTE_USER, simple("${body.idUsager}", String.class))
                .setHeader(UUID, exchangeProperty(UUID))
                .marshal(pojoToJson())
                .log("Requete HEAD envoyee a Jway")
                .to("rest:head:document/ds/{uuid}/attachment")
                .log(CODE_REPONSE)
                .log("Jeton CSRF obtenu = ${headers.X-CSRF-Token}")
                .setProperty(CSRF_TOKEN, simple("${headers.X-CSRF-Token}", String.class));

        // ajout d'un document a une demarche, phase 3 : requete proprement dite d'envoi à Jway
        from("direct:nouveau-document-phase-3").id("nouveau-document-phase-3")
                .log("* ROUTE nouveau-document-phase-3")
                .setHeader(X_CSRF_TOKEN, exchangeProperty(CSRF_TOKEN))
                .setHeader(REMOTE_USER, simple("${body.idUsager}", String.class))
                .setHeader(UUID, exchangeProperty(UUID))
                .setHeader(Exchange.CONTENT_TYPE, simple("multipart/form-data;boundary=" + MULTIPART_BOUNDARY))
                .process(newDocumentToJwayMapper)
                .log("Headers envoyes a Jway = ${headers}")
                .enrich("direct:log-multipart-message", new OldExchangeStrategy())
                .to("rest:post:document/ds/{uuid}/attachment")
                .log(CODE_REPONSE);

        // creation d'un courrier : scission du courrier en "n" documents
        // note 1 : dans Jway il n'a pas d'entite de courrier, il n'y a que "n" entites de documents ; chaque document
        //          possede les donnees du courrier, ce qui permet d'identifier les documents constituant un courrier.
        // note 2 : a partir du split() ci-dessous, on a "n" traitements, cad qu'il y a une iteration invisible
        from("direct:nouveau-courrier").id("nouveau-courrier")
                .log("* ROUTE nouveau-courrier")
                .unmarshal(jsonToPojo(NewCourrier.class))
                .bean(new NewCourrierValidator(allowedMimeTypes))
                .bean(new NewCourrierKeySetter())
                .split().method(NewCourrierSplitter.class, "splitCourrier")
                .log("Split OK")
                .setProperty("remoteUser", simple("${body.idUsager}", String.class))
                .choice()
                    .when(simple("${body.idDemarcheSiMetier} == null"))
                        .log("Document [${body.libelleDocument}] : courrier pas lie a une demarche, on passe directement a la phase d'envoi")
                    .otherwise()
                        .log("Document [${body.libelleDocument}] : courrier lie a la demarche [${body.idDemarcheSiMetier}]")
                        .enrich("direct:nouveau-document-phase-1", new PropertyPropagationStrategy(UUID))
                .end()
//                .enrich("direct:nouveau-document-phase-2", new PropertyPropagationStrategy(UUID, CSRF_TOKEN))   // Pas necessaire pour l'instant (mode alpha)
                .to("direct:nouveau-courrier-document-phase-envoi")
                .log("OK nouveau-courrier");

        // creation du i-eme document d'un courrier, phase d'envoi : envoi du document à Jway
        from("direct:nouveau-courrier-document-phase-envoi").id("nouveau-courrier-document-phase-envoi")
                .log("* ROUTE nouveau-courrier-document-phase-envoi")
//                .setHeader(X_CSRF_TOKEN, exchangeProperty(CSRF_TOKEN))            // Pas necessaire pour l'instant (mode alpha)
                .setHeader(REMOTE_USER, simple("${body.idUsager}", String.class))
                .setHeader(UUID, exchangeProperty(UUID))
                .setHeader(Exchange.CONTENT_TYPE, simple("multipart/form-data;boundary=" + MULTIPART_BOUNDARY))
                .process(newCourrierDocumentToJwayMapper)
                .log("Headers envoyes a Jway = ${headers}")
                .enrich("direct:log-multipart-message", new OldExchangeStrategy())
                .to("rest:post:alpha/document")
                .log(CODE_REPONSE);

        // trace du message de creation de document envoye a Jway
        from("direct:log-multipart-message").id("log-multipart-message")
                .bean(new MultipartJwayBodyReducer(maxFileContentSize))
                .log(traceJsonToJway())
                .to("log:INFO?showHeaders=true");
    }

    /**
     * JSON Unmarshalling to POJO using Jackson taken from the Spring context
     * @param unmarshalType Target POJO class
     * @return Camel dataformat
     */
    private DataFormatDefinition jsonToPojo(Class<?> unmarshalType) {
        JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);
        json.setUnmarshalType(unmarshalType);
        json.setAutoDiscoverObjectMapper("true");
        return json;
    }

    /**
     * JSON Unmarshalling to List using Jackson taken from the Spring context
     * @param unmarshalType Target List item POJO class
     * @return Camel dataformat
     */
    private ListJacksonDataFormat jsonToList(Class<?> unmarshalType) {
        ListJacksonDataFormat json = new ListJacksonDataFormat(unmarshalType);
        json.setUnmarshalType(unmarshalType);
        json.setAutoDiscoverObjectMapper(true);
        return json;
    }

    /**
     * JSON Marshalling using Jackson taken from the Spring context
     * @return Camel dataformat
     */
    private DataFormatDefinition pojoToJson() {
        JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);
        json.setAutoDiscoverObjectMapper("true");
        return json;
    }

    private String traceJsonToJway() {
        return "JSON envoye a Jway (" + formServicesUrl + ") = ${body}";
    }

}
