package ch.ge.ael.enu.mediation.metier.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Donnees contenues dans un message JSON de changement d'etat d'une demande existante.
 * <p>
 * Reference : ***REMOVED***/browse/ENU-427
 */
@Data
public class StatusChange {

    private String idPrestation;

    private String idUsager;

    private String idClientDemande;

    /**
     * Seules les valeurs "EN_TRAITEMENT" et "TERMINEE" sont acceptees.
     */
    private String nouvelEtat;

//    private LocalDate dateNouvelEtat;
    private String dateNouvelEtat;

    private String libelleSousEtat;

    /**
     * Seules les valeurs ENRICHISSEMENT_DE_DEMANDE et REPONSE_DEMANDE_RENSEIGNEMENT sont acceptees.
     */
    private String typeAction;

    private String urlAction;

    private String libelleAction;

    private String echeanceAction;

    private String urlRenouvellementDemarche;

}
