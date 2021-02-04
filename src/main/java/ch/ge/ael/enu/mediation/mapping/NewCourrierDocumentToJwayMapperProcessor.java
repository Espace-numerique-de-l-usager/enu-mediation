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
 * Cas d'usage : creation dans Jway du i-eme document constituant un courrier. Rappel : un courrier dans Jway
 * n'existe que par ses documents
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
        String categorie = exchange.getIn().getHeader(DemarcheRouter.CATEGORIE, String.class);   // dependance a changer
        String demarcheId = exchange.getIn().getHeader(DemarcheRouter.UUID, String.class);       // dependance a changer
        LOGGER.info("Dans {} - uuid demarche = [{}], categorie = [{}]", getClass().getSimpleName(), demarcheId, categorie);

        // preparation des donnees
        byte[] decodedContentAsBytes = Base64.getDecoder().decode(courrierDoc.getContenu());
        String mime = courrierDoc.getMime();
        String name = courrierDoc.getLibelleDocument() + "|" + courrierDoc.getIdDocumentSiMetier();
        String fileName = courrierDoc.getLibelleDocument() + "." + MimeUtils.getFileExtension(courrierDoc.getMime());
        fileName = "\"" + new FileNameSanitizer(fileNameSanitizationRegex).sanitize(fileName) + "\"";
        // note : l'upload va supprimer les caracteres accentues
        LOGGER.info("fileName apres assainissement = [{}]", fileName);

        // pour le champ "name", il faut creer un ContentType UTF-8, sinon les accents sont mal transmis
        ContentType textPlainUtf8 = ContentType.create("text/plain", MIME.UTF8_CHARSET);

        // construction de la requete multipart
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setBoundary(MULTIPART_BOUNDARY);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("name", name, textPlainUtf8);
        builder.addTextBody("type", JwayDocumentType.OTHER.name());
LOGGER.warn("ON FOUT EN FORCE LA CATEGORIE !");
//        builder.addTextBody("tag", categorie);
        builder.addTextBody("tag", "categorie_A");
        if (demarcheId != null) {
            builder.addTextBody("folder", demarcheId);
        }
//        builder.addTextBody("folderLabel", courrierDoc.getLibelleCourrier());
        builder.addTextBody("subtype", courrierDoc.getLibelleCourrier());
        builder.addBinaryBody("files", decodedContentAsBytes, ContentType.create(mime), fileName);

        // ajout de la requete multipart au body
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.build().writeTo(out);
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        exchange.getIn().setBody(in);
    }

}
