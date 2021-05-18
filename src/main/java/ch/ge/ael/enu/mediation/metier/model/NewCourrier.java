package ch.ge.ael.enu.mediation.metier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Donnees contenues dans un message JSON de creation d'un courrier, lie ou non a une demarche existante.
 * Reference : <a href="***REMOVED***/browse/ENU-425">ENU-424</a>.
 */
public class NewCourrier {

    private String idPrestation = null;

    private String idUsager = null;

    private String idDemarcheSiMetier = null;

    private String libelleCourrier = null;

    private List<NewDocument> documents = null;

    /**
     * Champ cree par la mediation, ajoute' ici par simplicite.
     */
    private String clef = null;

}
