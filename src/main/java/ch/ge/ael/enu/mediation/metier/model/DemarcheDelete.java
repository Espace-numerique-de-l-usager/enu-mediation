package ch.ge.ael.enu.mediation.metier.model;

import lombok.Data;
import org.apache.camel.Header;

@Data
public class DemarcheDelete {

    private String idPrestation = null;

    private String idUsager = null;

    private String idDemarcheSiMetier = null;

    public DemarcheDelete newDemarcheDelete(
                          @Header("idDemarcheSiMetier") String idDemarcheSiMetier,
                          @Header("idPrestation") String idPrestation,
                          @Header("idUsager") String idUsager) {
        this.idDemarcheSiMetier = idDemarcheSiMetier;
        this.idPrestation = idPrestation;
        this.idUsager = idUsager;
        return this;
    }

}
