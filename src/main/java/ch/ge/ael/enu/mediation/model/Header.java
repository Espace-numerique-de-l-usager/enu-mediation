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
package ch.ge.ael.enu.mediation.model;

import org.springframework.http.HttpHeaders;

/**
 * Pour les autres en-tetes, notamment HTTP, voir {}@link {@link HttpHeaders}.
 */
public class Header {

    /**
     * Contexte : RabbitMQ.
     * Le contenu du message, habituellement du JSON.
     */
    public static final String CONTENT_TYPE = "ContentType";

    /**
     * Contexte : RabbitMQ.
     * L'identifiant de correlation, permettant au producteur de faire le lien entre le message de reponse
     * de la mediation et son message d'origine.
     */
    public static final String CORRELATION_ID = "CorrelationId";

    /**
     * Contexte : RabbitMQ.
     * L'identifiant du SI metier producteur du message d'origine.
     * Cet identifiant permet a la mediation de router le message de reponse vers la queue du bon SI metier.
     */
    public static final String SI_METIER = "SiMetier";

    /**
     * Contexte : RabbitMQ.
     * Permet d'enrichir le message rejete dans la DLQ apres une erreur technique.
     */
    public static final String TECHNICAL_ERROR = "TechnicalError";

    /**
     * Contexte : HTTP.
     * Necessaire pour certains appels REST à FormServices.
     */
    public static final String X_CSRF_TOKEN = "X-CSRF-Token";

    /**
     * Contexte : HTTP.
     * Permet d'appeler les services REST de FormServices en tant que le bon utilisateur.
     */
    public static final String REMOTE_USER = "remote_user";

    private Header() {
    }

}
