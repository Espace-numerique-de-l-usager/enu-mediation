package ch.ge.ael.enu.mediation.metier.model;

import lombok.Data;
import org.apache.camel.Header;

@Data
public class DemarcheDelete {

    private String idPrestation;

    private String idUsager;

    private String idDemarcheSiMetier;

    public DemarcheDelete newDemarcheDelete(
                          @Header("idClientDemande") String idDemarcheSiMetier,
                          @Header("idPrestation") String idPrestation,
                          @Header("idUsager") String idUsager) {
        this.idDemarcheSiMetier = idDemarcheSiMetier;
        this.idPrestation = idPrestation;
        this.idUsager = idUsager;
        return this;
    }

}
