package ch.ge.ael.enu.mediation.jway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    private Integer id;
    private String name;
    private Text nameLabel;
    private Text descriptionLabel;
    private List<String> tags;
}
