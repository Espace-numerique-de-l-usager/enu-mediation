package ch.ge.ael.enu.mediation.routes;

import ch.ge.ael.enu.mediation.jway.model.*;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

@Configuration
public class NewDemarcheToJwayMapper {

    public File newDemarcheToFile(NewDemarche newDemarche) {
        File file = new File();
        file.setName(newDemarche.getName());
        User owner = new User();
        owner.setName(newDemarche.getOwner());
        file.setOwner(owner);
        Application application = new Application();
        application.setName("EDGSmartOne_afl");
        file.setApplication(application);
        file.setWorkflowStatus(newDemarche.getStatus());
        file.setStatus(newDemarche.getStatus());
        Form form = new Form();
        form.setNameLabel(new Text("Label", new HashMap<>() {{
            put("fr", "Mon lien externe");
        }}));
        form.setUrls(new ArrayList<>() {{
            add(new FormUrl("https://site_externe/MON_URL_DE_TEST"));
        }});
        return file;
    }
}
