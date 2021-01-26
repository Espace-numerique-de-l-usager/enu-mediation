package ch.ge.ael.enu.mediation.jway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Text {

    private String label;

    private HashMap<String,String> texts;

}
