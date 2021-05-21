package ch.ge.ael.enu.mediation.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Donnees contenues dans un message JSON de changement d'etat d'une demarche existante.
 * <p>
 * Reference : ***REMOVED***/browse/ENU-427
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusChange {

    private String idPrestation = null;

    private String idUsager = null;

    private String idDemarcheSiMetier = null;

    private String nouvelEtat = null;

    private String dateNouvelEtat = null;

    private String libelleAction = null;

    private String typeAction = null;

    private String urlAction = null;

    private String dateEcheanceAction = null;

    private String urlRenouvellementDemarche = null;

}
