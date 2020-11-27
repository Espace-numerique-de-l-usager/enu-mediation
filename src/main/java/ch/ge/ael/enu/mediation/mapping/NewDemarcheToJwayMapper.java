package ch.ge.ael.enu.mediation.mapping;

import ch.ge.ael.enu.mediation.jway.model.*;
import ch.ge.ael.enu.mediation.metier.model.NewDemarche;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

@Configuration
public class NewDemarcheToJwayMapper {

    public File newDemarcheToFile(NewDemarche newDemarche) {
        File file = new File();
        file.setName(newDemarche.getIdClientDemande());
        User owner = new User();
        owner.setName(newDemarche.getIdUsager());
        file.setOwner(owner);
        Application application = new Application();
        application.setName(newDemarche.getIdPrestation());
        file.setApplication(application);
        file.setWorkflowStatus(new StatusMapper().mapStringToJway((newDemarche.getEtat())));
        file.setStatus(new StatusMapper().mapStringToJway((newDemarche.getEtat())));
        Form form = new Form();
        form.setNameLabel(new Text("Label", new HashMap<>() {{
            put("fr", newDemarche.getLibelleAction());
        }}));
        form.setUrls(new ArrayList<>() {{
            add(new FormUrl(newDemarche.getUrlAction()));
        }});
        return file;
    }

}
