package ch.ge.ael.enu.mediation.business.exception;

import java.util.List;

/**
 * Exception lancee quand une valeur n'appartient pas a une enum donnee.
 */
public class IllegalEnumValueException extends ValidationException {

    public IllegalEnumValueException(String value, List allowedValues, String fieldName) {
        super("La valeur \"" + value + "\" du champ \"" + fieldName + "\" est incorrecte."
                + " Les valeurs possibles sont : " + allowedValues);
    }

}
