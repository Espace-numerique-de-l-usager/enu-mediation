package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.business.domain.NewCourrierDocument;
import ch.ge.ael.enu.mediation.jway.model.JwayDocumentType;
import ch.ge.ael.enu.mediation.util.file.FileNameSanitizer;
import ch.ge.ael.enu.mediation.util.mime.MimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;

@Slf4j
public class NewCourrierDocumentToJwayMapper extends AbstractDocumentToJwayMapper {

    public NewCourrierDocumentToJwayMapper(String fileNameSanitizationRegex) {
        super(fileNameSanitizationRegex);
    }

    public HttpEntity map(NewCourrierDocument courrierDoc, String demarcheId) {
        String categorie = courrierDoc.getIdPrestation();

        // preparation des donnees : bytes du contenu
        byte[] decodedContentAsBytes = null;
        if (courrierDoc.getContenu() != null) {
            // cas d'un document a mettre en GED
            decodedContentAsBytes = Base64.getDecoder().decode(courrierDoc.getContenu());
        }

        // preparation des donnees : mime
        //        String mime = courrierDoc.getMime();

        // preparation des donnees : name
        String name = courrierDoc.getLibelleDocument()
                + "|" + courrierDoc.getIdDocumentSiMetier()
                + "|" + courrierDoc.getIndex()
                + "|" + courrierDoc.getNbDocuments();
        if (courrierDoc.getGed() != null) {
            // cas d'un document deja en GED
            name = name
                    + "|" + courrierDoc.getGed().getFournisseur()
                    + "|" + courrierDoc.getGed().getVersion()
                    + "|" + courrierDoc.getGed().getIdDocument()
                    + "|" + courrierDoc.getGed().getAlgorithmeHash()
                    + "|" + courrierDoc.getGed().getHash();
        }

        // preparation des donnees : fileName
        String fileName = courrierDoc.getLibelleDocument() + "." + MimeUtils.getFileExtension(courrierDoc.getMime());
        fileName = "\"" + new FileNameSanitizer(fileNameSanitizationRegex).sanitize(fileName) + "\"";
        // note : l'upload va supprimer les caracteres accentues
        log.info("fileName apres assainissement = [{}]", fileName);

        // pour les champs contenant du texte, il faut creer un ContentType UTF-8, sinon les accents sont mal transmis
//        ContentType textPlainUtf8 = ContentType.create("text/plain", MIME.UTF8_CHARSET);

        // construction de la requete multipart
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("source", courrierDoc.getClefCourrier());
        body.add("name", name);
        body.add("type", JwayDocumentType.OTHER.name());
        if (demarcheId == null) {
            // courrier non lie a une demarche
            body.add("tag", categorie);
        } else {
            // courrier lie a une demarche
            body.add("fileUuid", demarcheId);
        }
        body.add("subtype", courrierDoc.getLibelleCourrier());

        if (courrierDoc.getContenu() != null) {
            // cas d'un document a mettre en GED
            HttpHeaders partHeaders = new HttpHeaders();
            partHeaders.setContentType(MediaType.TEXT_PLAIN);
            ByteArrayResource byteArrayResource = new CustomByteArrayResource(decodedContentAsBytes, fileName);
            HttpEntity<ByteArrayResource> partEntity = new HttpEntity<>(byteArrayResource, partHeaders);
            body.add("files", partEntity);
        }

        HttpHeaders headers = createHeaders(courrierDoc.getIdUsager());

        return new HttpEntity(body, headers);
    }

}
