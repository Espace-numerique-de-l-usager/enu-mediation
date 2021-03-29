package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.Application;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.jway.model.Form;
import ch.ge.ael.enu.mediation.jway.model.FormUrl;
import ch.ge.ael.enu.mediation.jway.model.User;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;

import java.util.ArrayList;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.BROUILLON;

public class NewDemarcheToJwayMapper {

    public File newDemarcheToFile(NewDemarche newDemarche) {
        File file = new File();

        // hack : si la demarche est un brouillon, on ajoute "DRAFT" au nom de la demarche.
        // Sans cette distinction, lors de la creation d'une demarche a l'etat "Deposee", l'application enu-backend
        // enverra coup sur coup 2 courriels a l'usager :
        // - un courriel (inutile) indiquant qu'un brouillon a ete cree
        // - un courriel (approprie) indiquant qu'une demarche a ete deposee
        if (newDemarche.getEtat().equals(BROUILLON.name())) {
            file.setName("(DRAFT)" + newDemarche.getIdDemarcheSiMetier());
        } else {
            file.setName(newDemarche.getIdDemarcheSiMetier());
        }

        User owner = new User();
        owner.setName(newDemarche.getIdUsager());
        file.setOwner(owner);

        Application application = new Application();
        application.setName(newDemarche.getIdPrestation());
        file.setApplication(application);

        String jwayStatus = new StatusMapper().mapStringToJway(newDemarche.getEtat());
        file.setWorkflowStatus(jwayStatus);
        file.setStatus(jwayStatus);

        if (is(newDemarche, BROUILLON)) {
            if (newDemarche.getLibelleAction() != null) {
                file.setStepDescription("|" + newDemarche.getLibelleAction());
            }

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
