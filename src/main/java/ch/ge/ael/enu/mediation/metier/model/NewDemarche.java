package ch.ge.ael.enu.mediation.metier.model;

import lombok.Data;

import java.util.UUID;

@Data
/**
 * Donnees contenues dans un message JSON de creation d'une nouvelle demande.
 * Reference : ***REMOVED***/browse/ENU-422
 */
public class NewDemarche {

    private String idPrestation;

    private String idUsager;

    private String idClientDemande;

    private String etat;

    private String urlAction;

    private String libelleAction;

    private String echeanceAction;
}
