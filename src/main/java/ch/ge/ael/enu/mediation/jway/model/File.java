package ch.ge.ael.enu.mediation.jway.model;

import ch.ge.ael.enu.mediation.serialization.JwayDateDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class File {

    private Integer id = null;

    private UUID uuid = null;

    private String name = null;

    private User owner = null;

    private Application application = null;

    private String workflowStatus = null;

    private String status = null;

    private Form form = null;

    private String step = null;

    private String stepDescription = null;

    @JsonDeserialize(using = JwayDateDeserializer.class)
    private LocalDateTime stepDate = null;

    private Boolean validated = null;

    @JsonDeserialize(using = JwayDateDeserializer.class)
    private LocalDateTime fromDate = null;

    /**
     * Ne pas prendre une LocalDateTime, sinon HTTP 400 lors de l'envoi a Jway.
     */
//    @JsonDeserialize(using = JwayDateDeserializer.class)
//    private LocalDateTime toDate;
    private String toDate = null;

    private String redirectUrl = null;

    @JsonDeserialize(using = JwayDateDeserializer.class)
    private LocalDateTime lastUpdate = null;
}
