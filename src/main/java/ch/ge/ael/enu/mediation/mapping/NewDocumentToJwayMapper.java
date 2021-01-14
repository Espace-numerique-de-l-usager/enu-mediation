package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import ch.ge.ael.enu.mediation.util.mime.MimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

/**
 * Cree le body de la requete multipart pour Jway.
 */
@Configuration
public class NewDocumentToJwayMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDocumentToJwayMapper.class);

    public static final String MULTIPART_BOUNDARY = "----FormBoundaryForEnuMediation";

    public String newDocumentToBody(NewDocument newDocument) {

        /*
         Exemple de body :

------WebKitFormBoundaryobO4oSaJlS4M34fz
Content-Disposition: form-data; name="form"

monForm
------WebKitFormBoundaryobO4oSaJlS4M34fz
Content-Disposition: form-data; name="files"; filename="pipo.txt"
Content-Type: text/plain

Salut camarade !
------WebKitFormBoundaryobO4oSaJlS4M34fz--

         */

        final String DASH_MULTIPART_BOUNDARY = "--" + MULTIPART_BOUNDARY;
        final String SEPARATOR = "\r\n";

        byte[] decodedContentAsBytes = Base64.getDecoder().decode(newDocument.getContenu());
        String decodedContent = new String(decodedContentAsBytes);
        LOGGER.info("Taille du fichier : {} bytes", decodedContent.length());

        String fileName = newDocument.getLibelleDocument() + "." + MimeUtils.getFileExtension(newDocument.getMime());

        String body = new StringBuilder()
                // champ "form"
                .append(DASH_MULTIPART_BOUNDARY).append(SEPARATOR)
                .append("Content-Disposition: form-data; name=\"form\"").append(SEPARATOR)
                .append(SEPARATOR)
                .append(newDocument.getTypeDocument()).append(SEPARATOR)   // a controler

                // champ "files" avec le fichier proprement dit
                .append(DASH_MULTIPART_BOUNDARY).append(SEPARATOR)
                .append("Content-Disposition: form-data; name=\"files\"; filename=\"").append(fileName).append("\"").append(SEPARATOR)
                .append("Content-Type: ").append(newDocument.getMime()).append(SEPARATOR)
                .append(SEPARATOR)
                .append(decodedContent).append(SEPARATOR)

                // fin de message
                .append(DASH_MULTIPART_BOUNDARY).append("--").append(SEPARATOR)
                .toString();

        // trace dangereuse, a ne pas envoyer en prod, meme en DEBUG !
        LOGGER.info("body = \n{}", body);

        return body;
    }

}
