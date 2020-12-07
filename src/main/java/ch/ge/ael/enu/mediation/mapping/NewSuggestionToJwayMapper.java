package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.Application;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.jway.model.Form;
import ch.ge.ael.enu.mediation.jway.model.FormUrl;
import ch.ge.ael.enu.mediation.jway.model.User;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewSuggestion;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class NewSuggestionToJwayMapper {

    public File newDemarcheToFile(NewSuggestion newSuggestion) {
        File file = new File();

        User owner = new User();
        owner.setName(newSuggestion.getIdUsager());
        file.setOwner(owner);

        Application application = new Application();
        application.setName("suggestion2");
        file.setApplication(application);

        String jwayStatus = new StatusMapper().mapStringToJway(DemarcheStatus.BROUILLON.name());
        file.setWorkflowStatus(jwayStatus);
        file.setStatus(jwayStatus);

        file.setStepDescription(newSuggestion.getDescriptionAction() + "|" + newSuggestion.getLibelleAction());

        file.setToDate(newSuggestion.getDateEcheanceAction());

        Form form = new Form();
        file.setForm(form);
        form.setUrls(new ArrayList<>());
        FormUrl formUrl = new FormUrl();
        form.getUrls().add(formUrl);
        formUrl.setBaseUrl(newSuggestion.getUrlAction());

        return file;
    }

}
