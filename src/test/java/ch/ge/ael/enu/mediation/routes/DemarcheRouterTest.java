package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.MediationApplication;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static ch.ge.ael.enu.mediation.routes.DemarcheRouter.RABBITMQ_QUEUE;

@SpringBootTest(classes = MediationApplication.class)
public class DemarcheRouterTest extends CamelTestSupport {

    @EndpointInject(RABBITMQ_QUEUE)
    protected MockEndpoint queue;

    @Autowired
    private ProducerTemplate template;

    @Test
    void sendDemandeBrouillon() {
        template.sendBody(RABBITMQ_QUEUE, "{\"idPrestation\": \"EDGSmartOne_afl\", \"idUsager\": \"DUBOISPELERINY\", \"idClientDemande\": \"Dossier-pipo-13\", \"etat\": \"BROUILLON\", \"urlAction\": \"http://www.tdg.ch\", \"libelleAction\": \"Prendre le tram\", \"echeanceAction\": \"2021-02-18\"}");

CamelTe
    }


}
