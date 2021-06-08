package ch.ge.ael.enu.mediation.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Donnees contenues dans un message JSON de creation d'une nouvelle suggestion de demarche.
 * Reference : JIRA ENU-369.
 */
public class NewSuggestion {

    private String idPrestation = null;

    private String idUsager = null;

    /** Texte du bouton. */
    private String libelleAction = null;

    /** URL du bouton. */
    private String urlAction = null;

    private String dateEcheanceAction = null;

    /** Description complete de l'action. */
    private String descriptionAction = null;

    /** URL du livret de la prestation. */
    private String urlPrestation = null;

}
