package ch.ge.ael.enu.mediation.jway.model;

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
    private LocalDateTime stepDate;
    private Boolean validated;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String redirectUrl;
}
