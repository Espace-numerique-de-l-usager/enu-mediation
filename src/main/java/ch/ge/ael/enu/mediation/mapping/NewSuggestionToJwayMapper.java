package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.Application;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.jway.model.Form;
import ch.ge.ael.enu.mediation.jway.model.FormUrl;
import ch.ge.ael.enu.mediation.jway.model.User;
import ch.ge.ael.enu.mediation.business.domain.NewSuggestion;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static ch.ge.ael.enu.mediation.jway.model.Status.BLANK;

@Configuration
public class NewSuggestionToJwayMapper {

    public File newDemarcheToFile(NewSuggestion newSuggestion) {
        File file = new File();

        file.setName("suggestion-" + newSuggestion.getIdPrestation() + "-" + ZonedDateTime.now().toEpochSecond());

        User owner = new User();
        owner.setName(newSuggestion.getIdUsager());
        file.setOwner(owner);

        Application application = new Application();
        application.setName(newSuggestion.getIdPrestation());
        file.setApplication(application);

        file.setWorkflowStatus(BLANK.name());
        file.setStatus(BLANK.name());

        file.setStepDescription(newSuggestion.getDescriptionAction() + "|" + newSuggestion.getLibelleAction() +
                "|" + newSuggestion.getUrlPrestation());

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
