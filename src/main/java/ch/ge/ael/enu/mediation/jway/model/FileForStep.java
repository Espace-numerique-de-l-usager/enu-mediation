package ch.ge.ael.enu.mediation.jway.model;

import lombok.Data;

@Data
public class FileForStep {

    private String step;

    private Form form;

    /*
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime lastUpdate;
     */
    private String lastUpdate;

}
