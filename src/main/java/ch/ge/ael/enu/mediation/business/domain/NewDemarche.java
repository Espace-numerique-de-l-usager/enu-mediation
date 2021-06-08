package ch.ge.ael.enu.mediation.business.domain;

import ch.ge.ael.enu.mediation.serialization.IsoDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Donnees contenues dans un message JSON de creation d'une nouvelle demarche.
 * <br/>
 * Reference : JIRA ENU-422.
 */
public class NewDemarche {

    private String idPrestation;

    private String idUsager;

    private String idDemarcheSiMetier;

    private String etat;

    private String libelleAction;

    private String typeAction;

    private String urlAction;

    private String dateEcheanceAction;

    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    private LocalDateTime dateDepot;

    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    private LocalDateTime dateMiseEnTraitement;

}
