package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.AuthMe;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.routes.DemarcheRouter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Extrait d'un AuthMe la categorie associee a un idPestation.
 * L'idPrestation doit etre present dans un header "idPrestation".
 * Cette classe ajoute a l'exchange une propriete "categorie".
 */
@Slf4j
public class AuthMeToCategoryProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        AuthMe authMe = exchange.getIn().getBody(AuthMe.class);
        String idPrestation = exchange.getIn().getHeader(DemarcheRouter.ID_PRESTATION, String.class);  // dependance sur la classe DemarcheRouter : a changer
        String categorie = authMe.getApplications().stream()
                .filter(app -> app.getName().equals(idPrestation))
                .map(app -> app.getTags().get(0).getName())
                .findFirst()
                .orElseThrow(() -> new ValidationException("Pas trouve la categorie de l'application " + idPrestation));
        log.info("Categorie de la prestation [{}] = [{}]", idPrestation, categorie);
        exchange.setProperty(DemarcheRouter.CATEGORIE, categorie);
    }

}
