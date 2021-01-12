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

    private String idPrestation;

    private String idUsager;

    private String idDemarcheSiMetier;

    private String typeDocument;

    private String libelleDocument;

    private String idDocumentSiMetier;

    private String mime;

    private String contenu;

}
