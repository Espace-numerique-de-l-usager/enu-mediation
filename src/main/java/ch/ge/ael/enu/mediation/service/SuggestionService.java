package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.business.domain.v1_0.Suggestion;
import ch.ge.ael.enu.mediation.model.jway.File;
import ch.ge.ael.enu.mediation.mapping.SuggestionToJwayMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuggestionService {

    private final FormServicesApi formServicesApi;

    private final SuggestionToJwayMapper newSuggestionToJwayMapper = new SuggestionToJwayMapper();

    public void handleNewSuggestion(Suggestion newSuggestion) {
        // creation dans FormServices de la demarche a l'etat de pre-brouillon
        File file = newSuggestionToJwayMapper.map(newSuggestion);
        File createdFile = formServicesApi.postFile(file, newSuggestion.getIdUsager());
        log.debug("Suggestion créée, uuid = [{}]", createdFile.getUuid());
    }
}
