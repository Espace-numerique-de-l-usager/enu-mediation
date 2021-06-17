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

import ch.ge.ael.enu.mediation.business.domain.ActionType;

import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkAbsentIfOtherAbsent;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkDate;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkEnum;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkPresentIfOtherPresent;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSize;
import static ch.ge.ael.enu.mediation.business.validation.ValidationUtils.checkSizeUrl;

/**
 * Factorise la validation d'une "action", presente dans plusieurs messages JSON (NewDemarche, StatusChange).
 */
public class ActionValidator {

    static final String TYPE_ACTION = "typeAction";

    static final String URL_ACTION = "urlAction";

    static final String LIBELLE_ACTION = "libelleAction";

    static final String DATE_ECHEANCE_ACTION = "dateEcheanceAction";

    void validate(
            String libelleAction,
            String urlAction,
            String typeAction,
            String dateEcheanceAction) {

        if (libelleAction == null) {
            checkAbsentIfOtherAbsent(dateEcheanceAction, DATE_ECHEANCE_ACTION, libelleAction, LIBELLE_ACTION);
        } else {
            final int MAX_SIZE_LIBELLE_ACTION = 250;
            checkSize(libelleAction, 1, MAX_SIZE_LIBELLE_ACTION, LIBELLE_ACTION);
            checkPresentIfOtherPresent(urlAction, URL_ACTION, libelleAction, LIBELLE_ACTION);
            checkEnum(typeAction, ActionType.class, TYPE_ACTION);
            if (urlAction == null) {
                checkAbsentIfOtherAbsent(dateEcheanceAction, DATE_ECHEANCE_ACTION, urlAction, URL_ACTION);
            } else {
                checkPresentIfOtherPresent(dateEcheanceAction, DATE_ECHEANCE_ACTION, urlAction, URL_ACTION);
                checkSizeUrl(urlAction, URL_ACTION);
                checkDate(dateEcheanceAction, DATE_ECHEANCE_ACTION);
            }
        }
    }

}
