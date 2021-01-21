package ch.ge.ael.enu.mediation.metier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Donnees contenues dans un message JSON de creation d'une nouvelle suggestion de demarche.
 * Reference : <a href="***REMOVED***/browse/ENU-369">ENU-369</a>.
 */
public class NewSuggestion {

    private String idPrestation;

    private String idUsager;

    /** Texte du bouton. */
    private String libelleAction;

    /** URL du bouton. */
    private String urlAction;

    private String dateEcheanceAction;

    /** Description complete de l'action. */
    private String descriptionAction;

    /** URL du livret de la prestation. */
    private String urlPrestation;

}
