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

import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActionValidatorTest {

    @Test
    void check1() {
        new ActionValidator().validate(null, null, null, null);
    }

    @Test
    void check2() {
        assertThatThrownBy(() -> new ActionValidator().validate(null, null, null, "2023-02-18"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le champ \"dateEcheanceAction\" ne peut pas être fourni quand le champ \"libelleAction\" n'est pas fourni");
    }

}
