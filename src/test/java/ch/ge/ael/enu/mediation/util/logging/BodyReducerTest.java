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
package ch.ge.ael.enu.mediation.util.logging;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BodyReducerTest {

    @Test
    void short_body_should_not_be_truncated() {
        String body = "{\"mime\": \"text/plain\", \"contenu\": \"U2FsdXQgY2FtYXJhZGUgIQ==\"}";
        String bytesReducedBody = new BodyReducer(30).reduceBody(body.getBytes());

        assertThat(bytesReducedBody).isEqualTo(body);
    }

    @Test
    void long_body_should_be_truncated() {
        String body = "{\"mime\": \"text/plain\", \"contenu\": \"77+977+977+977+9ABBKRklGAAEBAQBgAGAAAO+/ve+/vAGAAAOAGAAAOQAsRXhpZgAATU0AKgAAAAgAAQExAAIAAAAKAAAAGgAAAABHcmVlbnN\"}";
        String bytesReducedBody = new BodyReducer(30).reduceBody(body.getBytes());

        assertThat(bytesReducedBody).isEqualTo("{\"mime\": \"text/plain\", \"contenu\": \"77+977+977+977+9ABBKRklGAAEBAQBgAGAAAO+/ ... (champ tronque, car trop long) ... KAAAAGgAAAABHcmVlbnN\"}");
    }

}
