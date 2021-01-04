package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.IllegalEnumValueException;
import ch.ge.ael.enu.mediation.metier.exception.IllegalStringSizeException;
import ch.ge.ael.enu.mediation.metier.exception.MalformedDateException;
import ch.ge.ael.enu.mediation.metier.exception.MissingFieldException;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);

    private ValidationUtils() {
    }

    public static void checkExistence(Object value, String fieldName) {
        if (value instanceof String) {
            value = StringUtils.isBlank((String) value) ? null : value;
        }
        if (value == null) {
            LOGGER.info("Erreur : le champ [{}] manque dans le message", fieldName);
            throw new MissingFieldException(fieldName);
        }
    }

    public static <T> void checkEnum(String value, Class<T> clazz, String fieldName) {
        if (value != null) {
            if (!clazz.isEnum()) {
                throw new IllegalArgumentException("Cette methode doit etre appelee avec un parametre de type enum, mais "
                        + clazz + " n'est pas un type enum");
            }
            List<T> enumValues = Arrays.asList(clazz.getEnumConstants());
            boolean found = enumValues.stream()
                    .map(Object::toString)
                    .anyMatch(e -> e.equals(value));
            if (!found) {
                LOGGER.info("Erreur : la valeur [{}] du champ [{}] n'existe pas dans l'enum {}",
                        value, fieldName, clazz.getSimpleName());
                throw new IllegalEnumValueException(value, enumValues, fieldName);
            }
        }
    }

    public static void checkSize(String value, int minSize, int maxSize, String fieldName) {
        if (value != null) {
            value = value.trim();
            int size = value.length();
            if (size < minSize || size > maxSize) {
                LOGGER.info("Erreur : taille incorrecte de la valeur [{}] du champ [{}]. Intervalle permis : [{}, {}]",
                        value, fieldName, minSize, maxSize);
                throw new IllegalStringSizeException(value, minSize, maxSize, fieldName);
            }
        }
    }

    public static void checkDate(String value, String fieldName) {
        if (value != null) {
            try {
                LocalDate.parse(value);
            } catch (RuntimeException e) {
                LOGGER.info("Erreur : valeur incorrecte [{}] du champ date [{}]", value, fieldName);
                throw new MalformedDateException(value, fieldName);
            }
        }
    }

    public static void checkUrl(String value, String fieldName) {
        final int URL_MIN_LENGTH = 10;
        final int URL_MAX_LENGTH = 200;
        checkSize(value, URL_MIN_LENGTH, URL_MAX_LENGTH, fieldName);
    }

    /**
     * Leve une erreur si value est non null alors que otherValue est null.
     */
    public static void checkAbsentIfOtherAbsent(String value, String fieldName,
                                                String otherValue, String otherFieldName) {
        if (value != null && otherValue == null) {
            LOGGER.info("Erreur : le champ [{}] valant [{}] doit etre null quand le champ [{}] est null",
                    fieldName, value, otherFieldName);
            throw new ValidationException("Le champ \"" + fieldName + "\" ne peut pas être fourni quand le champ \""
                    + otherFieldName + "\" n'est pas fourni.");
        }
    }

    /**
     * Leve une erreur si value est null alors que otherValue est non null.
     */
    public static void checkPresentIfOtherPresent(String value, String fieldName,
                                                  String otherValue, String otherFieldName) {
        if (value == null && otherValue != null) {
            LOGGER.info("Erreur : le champ [{}] doit etre non null quand le champ [{}] valant [{}] est non null",
                    fieldName, otherFieldName, otherValue);
            throw new ValidationException("Le champ \"" + fieldName + "\" doit être fourni quand le champ \""
                    + otherFieldName + "\" est fourni.");
        }
    }

}
