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

import ch.ge.ael.enu.mediation.business.exception.MissingFieldException;
import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import ch.ge.ael.enu.mediation.business.domain.DemarcheStatus;
import ch.ge.ael.enu.mediation.business.domain.NewDemarche;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.business.domain.DemarcheStatus.BROUILLON;
import static ch.ge.ael.enu.mediation.business.domain.DemarcheStatus.DEPOSEE;
import static ch.ge.ael.enu.mediation.business.domain.DemarcheStatus.EN_TRAITEMENT;
import static ch.ge.ael.enu.mediation.business.domain.DemarcheStatus.TERMINEE;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkExistence;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdDemarcheSiMetier;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdPrestation;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeIdUsager;

/**
 * Verifie qu'un message JSON de creation d'une demarche est valide.
 */
public class NewDemarcheValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDemarcheValidator.class);

    private static final String ID_PRESTATION = "idPrestation";

    private static final String ID_USAGER = "idUsager";

    private static final String ID_DEMARCHE_SI_METIER = "idDemarcheSiMetier";

    private static final String ETAT = "etat";

    private static final String DATE_DEPOT = "dateDepot";

    private static final String DATE_MISE_EN_TRAITEMENT = "dateMiseEnTraitement";

    public NewDemarche validate(NewDemarche message) {
        final int MAX_SIZE_ETAT = 20;

        checkExistence(message.getIdPrestation(), ID_PRESTATION);
        checkExistence(message.getIdUsager(), ID_USAGER);
        checkExistence(message.getIdDemarcheSiMetier(), ID_DEMARCHE_SI_METIER);
        checkExistence(message.getEtat(), ETAT);

        checkSizeIdPrestation(message.getIdPrestation());
        checkSizeIdUsager(message.getIdUsager());
        checkSizeIdDemarcheSiMetier(message.getIdDemarcheSiMetier());
        checkSize(message.getEtat(), 1, MAX_SIZE_ETAT, ETAT);

        checkEnum(message.getEtat(), DemarcheStatus.class, ETAT);

        DemarcheStatus status = DemarcheStatus.valueOf(message.getEtat());
        if ((status == DEPOSEE || status == EN_TRAITEMENT) && message.getDateDepot() == null) {
            LOGGER.info("Erreur metier : champ [{}] obligatoire quand {} = {}", DATE_DEPOT, ETAT, status);
            throw new MissingFieldException(DATE_DEPOT,
                    "Ce champ est obligatoire quand " + ETAT + " = " + status);
        }
        if (status == EN_TRAITEMENT && message.getDateMiseEnTraitement() == null) {
            LOGGER.info("Erreur metier : champ [{}] obligatoire quand {} = {}", DATE_MISE_EN_TRAITEMENT, ETAT, status);
            throw new MissingFieldException(DATE_MISE_EN_TRAITEMENT,
                    "Ce champ est obligatoire quand " + ETAT + " = " + status);
        }
        if (status == TERMINEE) {
            LOGGER.info("Erreur metier : on ne peut pas creer de demarche a l'etat {}", TERMINEE);
            throw new ValidationException("On ne peut pas creer de demarche directement a l'etat " + TERMINEE);
        }

        if (status == BROUILLON) {
            ValidationUtils.checkAbsentIfOtherHasValue(message.getLibelleAction(), ActionValidator.LIBELLE_ACTION,
                    status.name(), ETAT, BROUILLON.name());
            ValidationUtils.checkPresentIfOtherHasValue(message.getUrlAction(), ActionValidator.URL_ACTION,
                    status.name(), ETAT, BROUILLON.name());
            ValidationUtils.checkAbsentIfOtherHasValue(message.getTypeAction(), ActionValidator.TYPE_ACTION,
                    status.name(), ETAT, BROUILLON.name());
        } else if (status == DEPOSEE) {
            ValidationUtils.checkAbsentIfOtherHasValue(message.getLibelleAction(), ActionValidator.LIBELLE_ACTION,
                    status.name(), ETAT, DEPOSEE.name());
        } else {
            new ActionValidator().validate(
                    message.getLibelleAction(),
                    message.getUrlAction(),
                    message.getTypeAction(),
                    message.getDateEcheanceAction());
        }

        LOGGER.info("Validation OK");
        return message;  // important !
    }

}
