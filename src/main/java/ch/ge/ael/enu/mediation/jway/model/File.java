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
    private Integer id;
    private UUID uuid;
    private String name;
    private User owner;
    private Application application;
    private String workflowStatus;
    private String status;
    private Form form;
    private String step;
    private String stepDescription;

    @JsonDeserialize(using = JwayDateDeserializer.class)
    private LocalDateTime stepDate;
    private Boolean validated;

    @JsonDeserialize(using = JwayDateDeserializer.class)
    private LocalDateTime fromDate;

    /**
     * Ne pas prendre une LocalDateTime, sinon HTTP 400 lors de l'envoi a Jway.
     */
//    @JsonDeserialize(using = JwayDateDeserializer.class)
//    private LocalDateTime toDate;
    private String toDate;

    private String redirectUrl;

    @JsonDeserialize(using = JwayDateDeserializer.class)
    private LocalDateTime lastUpdate;
}
