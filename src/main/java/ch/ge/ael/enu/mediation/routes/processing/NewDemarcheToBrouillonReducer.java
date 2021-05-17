package ch.ge.ael.enu.mediation.routes.processing;

import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.metier.model.DemarcheStatus.BROUILLON;

/**
 * Reduit une NewDemarche de n'importe quel etat en une NewDemarche a l'etat de brouillon.
 */
public class NewDemarcheToBrouillonReducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDemarcheToBrouillonReducer.class);

    public NewDemarche reduce(NewDemarche newDemarche) {
        if (newDemarche.getEtat().equals(BROUILLON.name())) {
            // hack : si la demarche est un brouillon, on ajoute "DRAFT" au nom de la demarche.
            // Sans cette distinction, lors de la creation d'une demarche a l'etat "Deposee", l'application enu-backend
            // enverra coup sur coup 2 courriels a l'usager :
            // - un courriel (inutile) indiquant qu'un brouillon a ete cree
            // - un courriel (approprie) indiquant qu'une demarche a ete deposee
            newDemarche.setIdDemarcheSiMetier("(DRAFT)" + newDemarche.getIdDemarcheSiMetier());
        } else {
            newDemarche.setEtat(BROUILLON.name());
            newDemarche.setDateDepot(null);
            newDemarche.setDateMiseEnTraitement(null);
            newDemarche.setLibelleAction(null);
            newDemarche.setUrlAction(null);
            newDemarche.setTypeAction(null);
        }

        LOGGER.info("Reduction a l'etat de brouillon OK");
        return newDemarche;
    }

}
