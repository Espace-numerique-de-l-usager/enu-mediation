package ch.ge.ael.enu.mediation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class DemarcheServiceTest {

    @Autowired
    private ObjectMapper jackson;

    @Autowired
    private DemarcheService demarcheService;

    @Autowired
    private FormServicesApi formServicesApi;

    @Test
    void handleStatusChange() {
    }
}