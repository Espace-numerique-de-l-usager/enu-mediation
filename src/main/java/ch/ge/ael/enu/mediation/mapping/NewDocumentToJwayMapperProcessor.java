package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.JwayDocumentType;
import ch.ge.ael.enu.mediation.metier.model.DocumentType;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import ch.ge.ael.enu.mediation.util.mime.MimeUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static ch.ge.ael.enu.mediation.metier.model.DocumentType.JUSTIFICATIF;

/**
 * Cree le body de la requete <strong>multipart</strong> pour Jway.
 */
@Configuration
public class NewDocumentToJwayMapperProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDocumentToJwayMapperProcessor.class);

    public static final String MULTIPART_BOUNDARY = "----FormBoundaryForEnuMediation";

    @Override
    public void process(Exchange exchange) throws IOException {
        NewDocument newDocument = exchange.getIn().getBody(NewDocument.class);

        // preparation des donnees
        byte[] decodedContentAsBytes = Base64.getDecoder().decode(newDocument.getContenu());
        String mime = newDocument.getMime();
        String name = newDocument.getLibelleDocument() + "|" + newDocument.getIdDocumentSiMetier();
        String fileName = (newDocument.getLibelleDocument() + "." + MimeUtils.getFileExtension(newDocument.getMime()));
//                .replaceAll("\\s", "_");    // ne marche pas : l'upload enleve les underscores
        JwayDocumentType type = isJustificatif(newDocument) ? JwayDocumentType.ATTACHMENT : JwayDocumentType.REPORT;

        // pour le champ "name", il faut creer un ContentType UTF-8, sinon les accents sont mal transmis
        ContentType textPlainUtf8 = ContentType.create("text/plain", MIME.UTF8_CHARSET);

        // construction de la requete multipart
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setBoundary(MULTIPART_BOUNDARY);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("name", name, textPlainUtf8);
        builder.addTextBody("type", type.name());   // pas encore pret, dixit Julien F.
        builder.addBinaryBody("files", decodedContentAsBytes, ContentType.create(mime), fileName);

        // ajout de la requete multipart au body
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.build().writeTo(out);
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        exchange.getIn().setBody(in);
    }

    private boolean isJustificatif(NewDocument newDocument) {
        return DocumentType.valueOf(newDocument.getTypeDocument()) == JUSTIFICATIF;
    }

}
