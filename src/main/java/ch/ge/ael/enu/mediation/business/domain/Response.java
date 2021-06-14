package ch.ge.ael.enu.mediation.business.domain;

import lombok.Data;

/**
 * Reponse envoyee un a message.
 */
@Data
public class Response {

    private ResponseType resultat;

    private String description;

}
