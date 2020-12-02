package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import ch.ge.ael.enu.mediation.metier.model.StatusChange;

import java.time.format.DateTimeFormatter;

/**
 * Transforme une requete de creation de demarche (NewDemarche) en une requete de changement d'etat (StatusChange).
 * L'etat (par ex. SOUMIS) que doit avoir la requete de changement d'etat est donne en parametre du constructeur.
 */
public class NewDemarcheToStatusChangeMapper {

    private final DemarcheStatus demarcheStatus;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NewDemarcheToStatusChangeMapper(DemarcheStatus demarcheStatus) {
        this.demarcheStatus = demarcheStatus;
    }

    public StatusChange map(NewDemarche newDemarche) {
        StatusChange statusChange = new StatusChange();
        statusChange.setIdPrestation(newDemarche.getIdPrestation());
        statusChange.setIdUsager(newDemarche.getIdUsager());
        statusChange.setIdClientDemande(newDemarche.getIdClientDemande());
        statusChange.setNouvelEtat(demarcheStatus.name());
        if (demarcheStatus == DemarcheStatus.DEPOSEE) {
            statusChange.setDateNouvelEtat(newDemarche.getDateDepot().format(FORMATTER));
        } else if (demarcheStatus == DemarcheStatus.EN_TRAITEMENT) {
            statusChange.setDateNouvelEtat(newDemarche.getDateMiseEnTraitement().format(FORMATTER));
        }

        return statusChange;
    }

}
