package ch.ge.ael.enu.mediation.jway.model;

import ch.ge.ael.enu.mediation.serialization.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
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

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime stepDate;
    private Boolean validated;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime fromDate;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime toDate;

    private String redirectUrl;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime lastUpdate;
}
