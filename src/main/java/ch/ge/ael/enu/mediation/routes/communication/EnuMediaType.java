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
package ch.ge.ael.enu.mediation.routes.communication;

import ch.ge.ael.enu.business.domain.v1_0.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

public class EnuMediaType {

    public static final String BROUILLON_ABANDON = "application/brouillon-abandon-v1.0+json";
    public static final String BROUILLON_DEMARCHE = "application/brouillon-de-demarche-v1.0+json";
    public static final String COURRIER = "application/courrier-v1.0+json";
    public static final String COURRIER_BINAIRE = "application/courrier-binaire-v1.0+json";
    public static final String COURRIER_HORS_DEMARCHE = "application/courrier-hors-demarche-v1.0+json";
    public static final String COURRIER_HORS_DEMARCHE_BINAIRE = "application/courrier-hors-demarche-binaire-v1.0+json";
    public static final String DEMARCHE_ABANDONNEE = "application/demarche-abandonnee-v1.0+json";
    public static final String DEMARCHE_ACTION_REQUISE = "application/demarche-action-requise-v1.0+json";
    public static final String DEMARCHE_DEPOSEE = "application/demarche-deposee-v1.0+json";
    public static final String DEMARCHE_EN_TRAITEMENT = "application/demarche-en-traitement-v1.0+json";
    public static final String DEMARCHE_TERMINEE = "application/demarche-terminee-v1.0+json";
    public static final String DOCUMENT_ACCES = "application/document-acces-v1.0+json";
    public static final String DOCUMENT = "application/document-v1.0+json";
    public static final String DOCUMENT_BINAIRE = "application/document-binaire-v1.0+json";
    public static final String DOCUMENT_RECEPTION_MODE = "application/document-reception-mode-v1.0+json";
    public static final String RESPONSE = "application/response-v1.0+json";
    public static final String SEQUENCE_MESSAGES = "application/sequence-enu-v1.0+json";
    public static final String SUGGESTION_ABANDON = "application/suggestion-abandon-v1.0+json";
    public static final String SUGGESTION = "application/suggestion-v1.0+json";

    public static final HashMap<String, TypeReference<?>> typeReferenceMap = new HashMap<String, TypeReference<?>>() {{
        put(BROUILLON_ABANDON, new TypeReference<BrouillonAbandon>(){});
        put(BROUILLON_DEMARCHE, new TypeReference<BrouillonDemarche>(){});
        put(COURRIER, new TypeReference<Courrier>(){});
        put(COURRIER_BINAIRE, new TypeReference<CourrierBinaire>(){});
        put(COURRIER_HORS_DEMARCHE, new TypeReference<CourrierHorsDemarche>(){});
        put(COURRIER_HORS_DEMARCHE_BINAIRE, new TypeReference<CourrierHorsDemarcheBinaire>(){});
        put(DEMARCHE_ABANDONNEE, new TypeReference<DemarcheAbandonnee>(){});
        put(DEMARCHE_ACTION_REQUISE, new TypeReference<DemarcheActionRequise>(){});
        put(DEMARCHE_DEPOSEE, new TypeReference<DemarcheDeposee>(){});
        put(DEMARCHE_EN_TRAITEMENT, new TypeReference<DemarcheEnTraitement>(){});
        put(DEMARCHE_TERMINEE, new TypeReference<DemarcheTerminee>(){});
        put(DOCUMENT_ACCES, new TypeReference<DocumentAcces>(){});
        put(DOCUMENT, new TypeReference<DocumentUsager>(){});
        put(DOCUMENT_BINAIRE, new TypeReference<DocumentUsagerBinaire>(){});
        put(DOCUMENT_RECEPTION_MODE, new TypeReference<ModeReceptionDocuments>(){});
        put(RESPONSE, new TypeReference<Response>(){});
        put(SEQUENCE_MESSAGES, new TypeReference<SequenceMessages>(){});
        put(SUGGESTION_ABANDON, new TypeReference<SuggestionAbandon>(){});
        put(SUGGESTION, new TypeReference<Suggestion>(){});
    }};

    private EnuMediaType() {
    }

}
