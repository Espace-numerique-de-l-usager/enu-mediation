/*
 *
 *  * Espace numerique de l'usager - enu-mediation
 *  *
 *  * Copyright (C) 2021 Republique et canton de Geneve
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU Affero General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Affero General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ch.ge.ael.enu.mediation.service.technical;

import ch.ge.ael.enu.mediation.business.domain.Prestation;
import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

import static ch.ge.ael.enu.mediation.routes.communication.Header.SI_METIER;

@Service
@Slf4j
public class SecurityService {

    @Resource
    private DeserializationService deserializationService;

    @Value("${app.prestation.simetier}")
    private String simetierByPrestationJson;

    private Map<String, String> simetierByPrestation = null;

    @PostConstruct
    public void init() {
        simetierByPrestation = deserializationService.deserialize(simetierByPrestationJson.getBytes(), Map.class);
        log.info("Table prestation -> SI metier : {}", simetierByPrestation);
    }

    /**
     * Rend le SI metier auquel appartient la prestation indiquee, ou null si pas trouve'.
     */
    public String getSimetier(String idPrestation) {
        return simetierByPrestation.get(idPrestation);
    }

    /**
     * Verifie que le SI metier a l'origine du message a bien le droit d'utiliser la prestation indiquee
     * dans le message.
     */
    public void checkAuthorizedPrestation(Message message) {
        String idPrestation = deserializationService.deserialize(message.getBody(), Prestation.class).getIdPrestation();
        String siMetier = message.getMessageProperties().getHeader(SI_METIER);

        if (idPrestation == null) {
            log.info("Erreur metier : le champ [idPrestation] manque");
            throw new ValidationException("Le champ \"idPrestation\" manque");
        } else if (! simetierByPrestation.containsKey(idPrestation)) {
            log.info("Prestation [{}] inconnue", idPrestation);
            throw new ValidationException(getPrestationErrorMessage(idPrestation));
        } else if (! simetierByPrestation.get(idPrestation).equals(siMetier)) {
            log.warn("SECURITE : l'application [{}] tente de poster un message portant sur la prestation [{}] pour laquelle elle n'a pas de droit d'acces",
                    siMetier, idPrestation);
            throw new ValidationException(getPrestationErrorMessage(idPrestation));
        }
    }

    /**
     * On veille a fournir au producteur un message identique, que la prestation n'existe pas ou qu'elle existe mais
     * que le producteur n'y ait pas acces.
     */
    private String getPrestationErrorMessage(String idPrestation) {
        return String.format("La prestation \"%s\" n'existe pas ou alors vous n'y pas acces", idPrestation);
    }

}
