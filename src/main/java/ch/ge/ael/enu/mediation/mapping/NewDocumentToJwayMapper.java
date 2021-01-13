package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.metier.model.NewDocument;
import ch.ge.ael.enu.mediation.util.mime.MimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class NewDocumentToJwayMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDocumentToJwayMapper.class);

    public static final String MULTIPART_BOUNDARY = "------FormBoundaryForEnuMediation";

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

        byte[] decodedContentAsBytes = Base64.getDecoder().decode(newDocument.getContenu());
        String decodedContent = new String(decodedContentAsBytes);

        String fileName = newDocument.getLibelleDocument() + "." + MimeUtils.getFileExtension(newDocument.getMime());

        StringBuilder sb = new StringBuilder()
                // champ JSON "form"
                .append(MULTIPART_BOUNDARY).append("\n")
                .append("Content-Disposition: form-data; name=\"form\"").append("\n")
                .append("\n")     // ligne vide pour les en-tetes
                .append(newDocument.getTypeDocument()).append("\n")   // a controler

                // champ JSON "files" avec le fichier proprement dit
                .append(MULTIPART_BOUNDARY).append("\n")
                .append("Content-Disposition: form-data; name=\"files\"; filename=\"").append(fileName).append("\"").append("\n")
                .append("Content-Type: ").append(newDocument.getMime())
                .append("\n")
                .append("[message-part-body; type: ").append(newDocument.getMime()).append(", size: " ).append(decodedContent.length()).append(" bytes]").append("\n")
                .append("\n")
                .append(decodedContent).append("\n")

                // fin de message
                .append(MULTIPART_BOUNDARY).append("--");

        String body = sb.toString();
        // trace dangereuse, a ne pas envoyer en prod, meme en DEBUG !
        LOGGER.info("body = \n{}", body);

        return body;
    }

}
