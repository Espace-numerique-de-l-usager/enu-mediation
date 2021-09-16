package ch.ge.ael.enu.mediation.service;

import ch.ge.ael.enu.business.domain.v1_0.NewDemarche;
import ch.ge.ael.enu.business.domain.v1_0.StatusChange;
import ch.ge.ael.enu.mediation.jway.model.File;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToJwayMapper;
import ch.ge.ael.enu.mediation.mapping.NewDemarcheToStatusChangeMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class DemarcheServiceTest {

    @Autowired
    private ObjectMapper jackson;

    @Autowired
    private DemarcheService demarcheService;

    @Autowired
    private FormServicesApi formServicesApi;

    private final NewDemarcheToJwayMapper newDemarcheToJwayMapper = new NewDemarcheToJwayMapper();

    @Test
    void handleNewDemarche() throws IOException {
        NewDemarche newDemarche = jackson.readValue(ResourceUtils.getFile("classpath:fixtures/newDemarche.json"), NewDemarche.class);
        log.info("newDemarche = {}", newDemarche);
//        File test = formServicesApi.postFile(newDemarche);
//        log.info("Jway file = {}",test);
//        StatusChange statusChange = new NewDemarcheToStatusChangeMapper(DEPOSEE).map(newDemarche);
//        demarcheService.changeStatus(statusChange);
    }

    @Test
    void handleStatusChange() {
    }
}