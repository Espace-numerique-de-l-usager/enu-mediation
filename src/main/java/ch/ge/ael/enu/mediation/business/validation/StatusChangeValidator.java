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
package ch.ge.ael.enu.mediation.business.validation;

import ch.ge.ael.enu.mediation.business.domain.DemarcheStatus;
import ch.ge.ael.enu.mediation.business.domain.StatusChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdDemarcheSiMetier;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdUsager;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeUrl;

/**
 * Verifie qu'un message JSON de changement d'etat d'une demarche est valide.
 */
public class StatusChangeValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusChangeValidator.class);

    private static final String ID_PRESTATION = "idPrestation";

    private static final String ID_USAGER = "idUsager";

    private static final String ID_DEMARCHE_SI_METIER = "idDemarcheSiMetier";

    private static final String NOUVEL_ETAT = "nouvelEtat";

    private static final String DATE_NOUVEL_ETAT = "dateNouvelEtat";

    private static final String URL_RENOUVELLEMENT_DEMARCHE = "urlRenouvellementDemarche";

    public StatusChange validate(StatusChange message) {
        LOGGER.info("Dans {}", getClass().getSimpleName());

        final int MAX_SIZE_NOUVEL_ETAT = 20;

        checkExistence(message.getIdPrestation(), ID_PRESTATION);
        checkExistence(message.getIdUsager(), ID_USAGER);
        checkExistence(message.getIdDemarcheSiMetier(), ID_DEMARCHE_SI_METIER);
        checkExistence(message.getNouvelEtat(), NOUVEL_ETAT);
        checkExistence(message.getDateNouvelEtat(), DATE_NOUVEL_ETAT);

        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
        checkSize(message.getNouvelEtat(), 1, MAX_SIZE_NOUVEL_ETAT, NOUVEL_ETAT);
        checkSizeUrl(message.getUrlAction(), ActionValidator.URL_ACTION);
        checkSizeUrl(message.getUrlRenouvellementDemarche(), URL_RENOUVELLEMENT_DEMARCHE);

        checkEnum(message.getNouvelEtat(), DemarcheStatus.class, NOUVEL_ETAT);

        checkDate(message.getDateEcheanceAction(), ActionValidator.DATE_ECHEANCE_ACTION);

        new ActionValidator().validate(
                message.getLibelleAction(),
                message.getUrlAction(),
                message.getTypeAction(),
                message.getDateEcheanceAction());

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
