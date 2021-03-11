package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.JwayDocumentType;
import ch.ge.ael.enu.mediation.metier.model.NewCourrierDocument;
import ch.ge.ael.enu.mediation.routes.DemarcheRouter;
import ch.ge.ael.enu.mediation.util.file.FileNameSanitizer;
import ch.ge.ael.enu.mediation.util.mime.MimeUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Cree le body de la requete <strong>multipart</strong> pour Jway.
 * <p/>
 * Cas d'usage : creation dans Jway du i-eme document constituant un courrier.
 * Dans Jway, un courrier n'est materialise que par ses documents ; il n'existe pas de courrier en tant que tel. Chaque
 * document doit donc contenir toute l'information du courrier.
 */
@Component
public class NewCourrierDocumentToJwayMapperProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewCourrierDocumentToJwayMapperProcessor.class);

    public static final String MULTIPART_BOUNDARY = "----FormBoundaryForEnuMediation";

    @Value("${app.file.name.sanitization-regex}")
    private String fileNameSanitizationRegex;

    @Override
    public void process(Exchange exchange) throws IOException {
        NewCourrierDocument courrierDoc = exchange.getIn().getBody(NewCourrierDocument.class);
        String categorie = exchange.getProperty(DemarcheRouter.CATEGORIE, String.class);         // dependance sur la classe DemarcheRouter : a changer
        String demarcheId = exchange.getIn().getHeader(DemarcheRouter.UUID, String.class);       // dependance sur la classe DemarcheRouter : a changer
        LOGGER.info("Dans {} - uuid demarche = [{}], categorie = [{}]", getClass().getSimpleName(), demarcheId, categorie);

        // preparation des donnees
        byte[] decodedContentAsBytes = Base64.getDecoder().decode(courrierDoc.getContenu());
        String mime = courrierDoc.getMime();
        String name = courrierDoc.getLibelleDocument()
                + "|" + courrierDoc.getIdDocumentSiMetier()
                + "|" + courrierDoc.getIndex()
                + "|" + courrierDoc.getNbDocuments();
        String fileName = courrierDoc.getLibelleDocument() + "." + MimeUtils.getFileExtension(courrierDoc.getMime());
        fileName = "\"" + new FileNameSanitizer(fileNameSanitizationRegex).sanitize(fileName) + "\"";
        // note : l'upload va supprimer les caracteres accentues
        LOGGER.info("fileName apres assainissement = [{}]", fileName);

        // pour les champs contenant du texte, il faut creer un ContentType UTF-8, sinon les accents sont mal transmis
        ContentType textPlainUtf8 = ContentType.create("text/plain", MIME.UTF8_CHARSET);

        // construction de la requete multipart
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setBoundary(MULTIPART_BOUNDARY);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("source", courrierDoc.getClefCourrier());
        builder.addTextBody("name", name, textPlainUtf8);
        builder.addTextBody("type", JwayDocumentType.OTHER.name());
        if (demarcheId == null) {
            // courrier non lie a une demarche
            builder.addTextBody("tag", categorie);
        } else {
            // courrier lie a une demarche
            builder.addTextBody("fileUuid", demarcheId);
        }
        builder.addTextBody("subtype", courrierDoc.getLibelleCourrier(), textPlainUtf8);
        builder.addBinaryBody("files", decodedContentAsBytes, ContentType.create(mime), fileName);

        // ajout de la requete multipart au body
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.build().writeTo(out);
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        exchange.getIn().setBody(in);
    }

}
