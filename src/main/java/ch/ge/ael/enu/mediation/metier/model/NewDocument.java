package ch.ge.ael.enu.mediation.metier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Donnees contenues dans un message JSON d'ajout d'un document a une demarche existante.
 * Reference : <a href="***REMOVED***/browse/ENU-424">ENU-424</a>.
 */
public class NewDocument {

    private String idPrestation = null;

    private String idUsager = null;

    private String idDemarcheSiMetier = null;

    private String typeDocument = null;

    private String libelleDocument = null;

    private String idDocumentSiMetier = null;

    private String mime = null;

    private String contenu = null;

}
