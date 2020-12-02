package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.*;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.BROUILLON;

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

        if (is(newDemarche, BROUILLON) && newDemarche.getLibelleAction() != null) {
            String stepDescription = new StringBuilder()
                    .append("|")
                    .append(newDemarche.getLibelleAction())
//                    .append("|")
//                    .append(newDemarche.getUrlAction())
                    .toString();
            file.setStepDescription(stepDescription);
            file.setToDate(newDemarche.getDateEcheanceAction());

            Form form = new Form();
            file.setForm(form);
            form.setUrls(new ArrayList<>());
            FormUrl formUrl = new FormUrl();
            form.getUrls().add(formUrl);
            formUrl.setBaseUrl(newDemarche.getUrlAction());
        }

        return file;
    }

    private boolean is(NewDemarche newDemarche, DemarcheStatus status) {
        return DemarcheStatus.valueOf(newDemarche.getEtat()) == status;
    }

}
