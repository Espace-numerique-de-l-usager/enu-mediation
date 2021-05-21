package ch.ge.ael.enu.mediation.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Identifiants GED d'un document.
 */
public class GedData {

    private String fournisseur = null;

    private String version = null;

    private String idDocument = null;

    private String algorithmeHash = null;

    private String hash = null;

}
