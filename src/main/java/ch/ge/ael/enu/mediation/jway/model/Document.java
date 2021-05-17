package ch.ge.ael.enu.mediation.jway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    private UUID uuid = null;

}
