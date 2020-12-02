package ch.ge.ael.enu.mediation.metier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

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

    private String idClientDemande;

    private String nouvelEtat;

    private String dateNouvelEtat;

//    private String libelleSousEtat;

    /**
     * Seules les valeurs ENRICHISSEMENT_DE_DEMANDE et REPONSE_DEMANDE_RENSEIGNEMENT sont acceptees.
     */
    private String typeAction;

    private String urlAction;

    private String libelleAction;

    private String echeanceAction;

    private String urlRenouvellementDemarche;

}
