/*
 * Espace numerique de l'usager - enu-mediation
 *
 * Copyright (C) 2021 Republique et canton de Geneve
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.business.domain.v1_0.Suggestion;
import ch.ge.ael.enu.mediation.jway.model.Application;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.jway.model.Form;
import ch.ge.ael.enu.mediation.jway.model.FormUrl;
import ch.ge.ael.enu.mediation.jway.model.User;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static ch.ge.ael.enu.mediation.jway.model.Status.BLANK;

@Configuration
public class SuggestionToJwayMapper {

    public File map(Suggestion newSuggestion) {
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
        formUrl.setBaseUrl(newSuggestion.getUrlAction().toString());

        return file;
    }

}
