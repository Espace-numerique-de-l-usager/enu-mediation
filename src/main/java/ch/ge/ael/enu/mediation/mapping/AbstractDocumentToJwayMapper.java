package ch.ge.ael.enu.mediation.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;

@Slf4j
public class AbstractDocumentToJwayMapper {

    protected final String fileNameSanitizationRegex;

    public AbstractDocumentToJwayMapper(String fileNameSanitizationRegex) {
        this.fileNameSanitizationRegex = fileNameSanitizationRegex;
    }

    protected HttpHeaders createHeaders(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    public static class CustomByteArrayResource extends ByteArrayResource {

        private String fileName;

        public CustomByteArrayResource(byte[] fileContent, String fileName) {
            super(fileContent);
            this.fileName = fileName;
        }

        @Override
        public String getFilename() {
            return fileName;
        }
    }

}
