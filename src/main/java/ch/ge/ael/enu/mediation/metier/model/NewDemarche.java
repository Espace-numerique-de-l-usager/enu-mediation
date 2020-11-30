package ch.ge.ael.enu.mediation.metier.model;

import ch.ge.ael.enu.mediation.serialization.IsoDateDeserializer;
import ch.ge.ael.enu.mediation.serialization.IsoDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @JsonDeserialize(using = IsoDateDeserializer.class)
    private LocalDate dateEcheanceAction;

    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    private LocalDateTime dateDepot;

    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    private LocalDateTime dateMiseEnTraitement;

}
