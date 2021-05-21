package ch.ge.ael.enu.mediation.routes.processing;

import ch.ge.ael.enu.mediation.business.domain.NewDemarche;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.ge.ael.enu.mediation.business.domain.DemarcheStatus.BROUILLON;

/**
 * Pour une NewDemarche dans n'importe quel etat, cree une NewDemarche a l'etat de brouillon.
 */
public class NewDemarcheToBrouillonReducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewDemarcheToBrouillonReducer.class);

    public NewDemarche reduce(NewDemarche newDemarche) {
        NewDemarche draft = new NewDemarche();

        draft.setEtat(BROUILLON.name());
        draft.setIdPrestation(newDemarche.getIdPrestation());
        draft.setIdUsager(newDemarche.getIdUsager());

        if (newDemarche.getEtat().equals(BROUILLON.name())) {
            // hack : si la demarche est un brouillon, on ajoute "DRAFT" au nom de la demarche.
            // Sans cette distinction, lors de la creation d'une demarche a l'etat "Deposee", l'application enu-backend
            // enverra coup sur coup 2 courriels a l'usager :
            // - un courriel (inutile) indiquant qu'un brouillon a ete cree
            // - un courriel (approprie) indiquant qu'une demarche a ete deposee
            draft.setIdDemarcheSiMetier("(DRAFT)" + newDemarche.getIdDemarcheSiMetier());
            draft.setUrlAction(newDemarche.getUrlAction());
        } else {
            draft.setIdDemarcheSiMetier(newDemarche.getIdDemarcheSiMetier());
        }

        LOGGER.info("Reduction a l'etat de brouillon OK");
        return draft;
    }

}
