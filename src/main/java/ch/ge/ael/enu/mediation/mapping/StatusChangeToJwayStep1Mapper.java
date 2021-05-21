package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.FileForStep;
import ch.ge.ael.enu.mediation.jway.model.Form;
import ch.ge.ael.enu.mediation.jway.model.FormUrl;
import ch.ge.ael.enu.mediation.business.domain.DemarcheStatus;
import ch.ge.ael.enu.mediation.business.domain.StatusChange;

import java.util.ArrayList;

import static ch.ge.ael.enu.mediation.business.domain.DemarcheStatus.TERMINEE;

public class StatusChangeToJwayStep1Mapper {

    public FileForStep map(StatusChange statusChange) {
        FileForStep file = new FileForStep();

        file.setStep(new StatusMapper().mapStringToJway(statusChange.getNouvelEtat()));

        file.setLastUpdate(statusChange.getDateNouvelEtat());

        String baseUrl = extractBaseUrl(statusChange);
        if (baseUrl != null) {
            Form form = new Form();
            file.setForm(form);
            form.setUrls(new ArrayList<>());
            FormUrl formUrl = new FormUrl();
            form.getUrls().add(formUrl);
            formUrl.setBaseUrl(baseUrl);
        }

        if (statusChange.getTypeAction() != null) {
            StringBuilder sb = new StringBuilder()
                    .append(statusChange.getLibelleAction())
                    .append("|")
                    .append(statusChange.getTypeAction());
            file.setStepDescription(sb.toString());
        }

        return file;
    }

    private boolean isNewStatus(StatusChange statusChange, DemarcheStatus status) {
        return DemarcheStatus.valueOf(statusChange.getNouvelEtat()) == status;
    }

    /**
     * Extrait la valeur a mettre dans form.urls[0].baseUrl. Peut etre null
     */
    private String extractBaseUrl(StatusChange statusChange) {
        if (isNewStatus(statusChange, TERMINEE)) {
            return statusChange.getUrlRenouvellementDemarche();
        } else {
            return statusChange.getUrlAction();
        }
    }

}
