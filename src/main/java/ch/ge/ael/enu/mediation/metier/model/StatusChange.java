package ch.ge.ael.enu.mediation.metier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Donnees contenues dans un message JSON de changement d'etat d'une demande existante.
 * <p>
 * Reference : ***REMOVED***/browse/ENU-427
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusChange {

    private String idPrestation;

    private String idUsager;

    private String idDemarcheSiMetier;

    private String nouvelEtat;

    private String dateNouvelEtat;

    private String libelleAction;

    private String typeAction;

    private String urlAction;

    private String dateEcheanceAction;

    private String urlRenouvellementDemarche;

}
