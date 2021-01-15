package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.JwayDocumentType;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.DocumentType;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import ch.ge.ael.enu.mediation.util.mime.MimeUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static ch.ge.ael.enu.mediation.metier.model.DocumentType.JUSTIFICATIF;

/**
 * Cree le body de la requete <strong>multipart</strong> pour Jway.
 * <p/>
 * Ne fonctionne pas : on obtient une erreur HTTP 400. Differences avec la version NewDocumentToJwayMapper :
 * - l'en-tete principal vaut
 *     Content-Type=multipart/form-data;boundary=----FormBoundaryForEnuMediation (version OK, avec NewDocumentToJwayMapper)
 *     Content-Type=multipart/form-data                                          (version KO, avec cette classe-ci)
 * - dans la partie du fichier, en plus des en-tetes 'Content-Disposition: form-data; name="files"; filename="Un manifeste.jpg"'
 *   et 'Content-Type: image/jpeg', on a :
 *     (rien)                            (version OK, avec NewDocumentToJwayMapper)
 *     Content-Transfer-Encoding: binary (version KO, avec cette classe-ci)
 *  C'est surement la 1ere difference qui fait planter. Avec NewDocumentToJwayMapper, sans "boundary=...", ça plante aussi.
 *
 */
@Configuration
public class NewDocumentToJwayMapperProcessor implements Processor{

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDocumentToJwayMapperProcessor.class);

    public static final String MULTIPART_BOUNDARY = "----FormBoundaryForEnuMediation";

    @Override
    public void process(Exchange exchange) throws IOException {
        NewDocument newDocument = exchange.getIn().getBody(NewDocument.class);

        // preparation des donnees
        byte[] decodedContentAsBytes = Base64.getDecoder().decode(newDocument.getContenu());
        String decodedContent = new String(decodedContentAsBytes);
        LOGGER.info("Taille du fichier : {} bytes", decodedContent.length());
        String mime = newDocument.getMime();
        String name = newDocument.getLibelleDocument() + "|" + newDocument.getIdDocumentSiMetier();
        String fileName = (newDocument.getLibelleDocument() + "." + MimeUtils.getFileExtension(newDocument.getMime()));
//                .replaceAll("\\s", "_");    // ne marche pas : l'upload enleve les underscores
        LOGGER.info("fileName = {}", fileName);
        JwayDocumentType type = is(newDocument, JUSTIFICATIF) ? JwayDocumentType.ATTACHMENT : JwayDocumentType.REPORT;

        // construction de la requete multipart
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setBoundary(MULTIPART_BOUNDARY);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("name", name);
        builder.addTextBody("type", type.name());   // pas encore pret, dixit Julien F.
        builder.addBinaryBody("files", decodedContentAsBytes, ContentType.create(mime), fileName);

        // ajout de la requete multipart au body
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.build().writeTo(out);
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        exchange.getIn().setBody(in);
    }

    private boolean is(NewDocument newDocument, DocumentType type) {
        return DocumentType.valueOf(newDocument.getTypeDocument()) == type;
    }

}
