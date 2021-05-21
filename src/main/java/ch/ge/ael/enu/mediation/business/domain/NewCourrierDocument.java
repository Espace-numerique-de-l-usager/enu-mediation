package ch.ge.ael.enu.mediation.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Structure resultant de la scission d'un NewCourrier en n documents.
 * Cette classe definit un de ces n documents.
 * Plus exactement elle definit un objet contenant les informations de l'en-tete du courrier, plus les informations
 * d'un des documents contenus dans le courrier.
 * Les informations definissant l'en-tete du courrier sont donc dupliquees dans chaque document.
 */
public class NewCourrierDocument {

    private String idPrestation = null;

    private String idUsager = null;

    private String idDemarcheSiMetier = null;

    private String libelleCourrier = null;

    private String clefCourrier = null;

    private String idDocumentSiMetier = null;

    private String libelleDocument = null;

    private String mime = null;

    private String contenu = null;

    private GedData ged;

    /**
     * Position du document parmi tous les documents du courrier.
     */
    private int index;

    /**
     * Nombre de documents dans le courrier.
     */
    private int nbDocuments;

}
