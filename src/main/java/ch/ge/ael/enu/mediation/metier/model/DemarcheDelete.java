package ch.ge.ael.enu.mediation.metier.model;

import lombok.Data;
import org.apache.camel.Header;

@Data
public class DemarcheDelete {
    private String idPrestation;
    private String idUsager;
    private String idClientDemande;

    public DemarcheDelete newDemarcheDelete(@Header("idClientDemande") String idClientDemande,
                          @Header("idPrestation") String idPrestation,
                          @Header("idUsager") String idUsager) {
        this.idClientDemande = idClientDemande;
        this.idPrestation = idPrestation;
        this.idUsager = idUsager;
        return this;
    }
}
