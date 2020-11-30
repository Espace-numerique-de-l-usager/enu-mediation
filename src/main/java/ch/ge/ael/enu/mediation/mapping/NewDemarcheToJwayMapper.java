package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.*;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.BROUILLON;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.SOUMISE;
import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.EN_COURS;

@Configuration
public class NewDemarcheToJwayMapper {

    public File newDemarcheToFile(NewDemarche newDemarche) {
        File file = new File();

        file.setName(newDemarche.getIdClientDemande());

        User owner = new User();
        owner.setName(newDemarche.getIdUsager());
        file.setOwner(owner);

        Application application = new Application();
        application.setName(newDemarche.getIdPrestation());
        file.setApplication(application);

        file.setWorkflowStatus(new StatusMapper().mapStringToJway((newDemarche.getEtat())));

        file.setStatus(new StatusMapper().mapStringToJway((newDemarche.getEtat())));

        if (is(newDemarche, BROUILLON) && newDemarche.getUrlAction() != null) {
            StringBuilder sb = new StringBuilder()
                    .append("|")
                    .append(newDemarche.getLibelleAction())
                    .append("|")
                    .append(newDemarche.getUrlAction());
            file.setStepDescription(sb.toString());
            file.setToDate(newDemarche.getDateEcheanceAction());
        }
        /*
        else if (is(newDemarche, SOUMISE)) {
            file.setFromDate(newDemarche.getDateDepot());
        } else if (is(newDemarche, EN_COURS)) {
            file.setFromDate(newDemarche.getDateDepot());
            file.setToDate(newDemarche.getDateMiseEnTraitement());
        }
         */

        return file;
    }

    private boolean is(NewDemarche newDemarche, DemarcheStatus status) {
        return DemarcheStatus.valueOf(newDemarche.getEtat()) == status;
    }

}
