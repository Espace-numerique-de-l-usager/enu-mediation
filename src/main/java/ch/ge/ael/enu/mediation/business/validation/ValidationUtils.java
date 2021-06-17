/*
 * Espace numerique de l'usager - enu-mediation
 *
 * Copyright (C) 2021 Republique et canton de Geneve
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.ge.ael.enu.mediation.business.validation;

import ch.ge.ael.enu.mediation.business.exception.EmptyListException;
import ch.ge.ael.enu.mediation.business.exception.IllegalEnumValueException;
import ch.ge.ael.enu.mediation.business.exception.IllegalStringSizeException;
import ch.ge.ael.enu.mediation.business.exception.MalformedDateException;
import ch.ge.ael.enu.mediation.business.exception.MissingFieldException;
import ch.ge.ael.enu.mediation.business.exception.TooLargeListException;
import ch.ge.ael.enu.mediation.business.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ch.ge.ael.enu.mediation.util.logging.StringTruncationUtils.truncate;

public class ValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);

    private ValidationUtils() {
    }

    /**
     * Verifie qu'un champ est present.
     */
    public static void checkExistence(Object value, String fieldName) {
        if (value instanceof String) {
            value = StringUtils.isBlank((String) value) ? null : value;
        }
        if (value == null) {
            LOGGER.info("Erreur metier : le champ [{}] manque dans le message", fieldName);
            throw new MissingFieldException(fieldName);
        }
    }

    /**
     * Verifie que la valeur d'un champ est bien dans une certaine Enum.
     */
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
                LOGGER.info("Erreur metier : la valeur [{}] du champ [{}] n'existe pas dans l'enum {}",
                        value, fieldName, clazz.getSimpleName());
                throw new IllegalEnumValueException(value, enumValues, fieldName);
            }
        }
    }

    /**
     * Verifie que la valeur (String) d'un champ n'est ni trop courte, ni trop longue.
     */
    public static void checkSize(String value, int minSize, int maxSize, String fieldName) {
        if (value != null) {
            value = value.trim();
            int size = value.length();
            if (size < minSize || size > maxSize) {
                LOGGER.info("Erreur metier : taille incorrecte de la valeur [{}] du champ [{}]. Intervalle permis : [{}, {}]",
                        value, fieldName, minSize, maxSize);
                throw new IllegalStringSizeException(value, minSize, maxSize, fieldName);
            }
        }
    }

    /**
     * Verifie que la valeur (String) d'un champ contient bien une date (sans heures).
     */
    public static void checkDate(String value, String fieldName) {
        if (value != null) {
            try {
                LocalDate.parse(value);
            } catch (RuntimeException e) {
                LOGGER.info("Erreur metier : valeur incorrecte [{}] du champ date [{}]", value, fieldName);
                throw new MalformedDateException(value, fieldName);
            }
        }
    }

    /**
     * Leve une erreur si value est non nul alors que otherValue est nul.
     */
    public static void checkAbsentIfOtherAbsent(String value, String fieldName,
                                                String otherValue, String otherFieldName) {
        if (value != null && otherValue == null) {
            LOGGER.info("Erreur metier : le champ [{}] valant [{}] doit etre nul quand le champ [{}] est nul",
                    fieldName, value, otherFieldName);
            throw new ValidationException("Le champ \"" + fieldName + "\" ne peut pas être fourni quand le champ \""
                    + otherFieldName + "\" n'est pas fourni");
        }
    }

    /**
     * Leve une erreur si value est non nul alors que otherValue est non nul.
     */
    public static void checkAbsentIfOtherPresent(String value, String fieldName,
                                                 String otherValue, String otherFieldName) {
        if (value != null && otherValue != null) {
            LOGGER.info("Erreur metier : le champ [{}] valant [{}] doit etre nul quand le champ [{}] valant [{}] est non nul",
                    fieldName, value, otherFieldName, otherValue);
            throw new ValidationException("Le champ \"" + fieldName + "\" ne peut pas être fourni quand le champ \""
                    + otherFieldName + "\" est fourni");
        }
    }

    /**
     * Leve une erreur si value est non nul alors que otherValue vaut someOtherValue.
     */
    public static void checkAbsentIfOtherHasValue(String value, String fieldName,
                                                  String otherValue, String otherFieldName, String someOtherValue) {
        if (value != null && Objects.equals(otherValue, someOtherValue)) {
            LOGGER.info("Erreur metier : le champ [{}] doit etre nul quand le champ [{}] vaut [{}]",
                    fieldName, otherFieldName, someOtherValue);
            throw new ValidationException("Le champ \"" + fieldName + "\" ne peut pas être fourni quand le champ \""
                    + otherFieldName + "\" vaut \"" + someOtherValue + "\"");
        }
    }

    /**
     * Leve une erreur si value est nul alors que otherValue est non nul.
     */
    public static void checkPresentIfOtherPresent(String value, String fieldName,
                                                  String otherValue, String otherFieldName) {
        if (value == null && otherValue != null) {
            LOGGER.info("Erreur metier : le champ [{}] doit etre non nul quand le champ [{}] valant [{}] est non nul",
                    fieldName, otherFieldName, otherValue);
            throw new ValidationException("Le champ \"" + fieldName + "\" doit être fourni quand le champ \""
                    + otherFieldName + "\" est fourni");
        }
    }

    /**
     * Leve une erreur si value est nul alors que otherValue vaut someOtherValue.
     */
    public static void checkPresentIfOtherHasValue(String value, String fieldName,
                                                   String otherValue, String otherFieldName, String someOtherValue) {
        if (value == null && Objects.equals(otherValue, someOtherValue)) {
            LOGGER.info("Erreur metier : le champ [{}] doit etre non nul quand le champ [{}] vaut [{}]",
                    fieldName, otherFieldName, someOtherValue);
            throw new ValidationException("Le champ \"" + fieldName + "\" doit etre fourni quand le champ \""
                    + otherFieldName + "\" vaut \"" + someOtherValue + "\"");
        }
    }

    /**
     * Leve une erreur si la condition suivante n'est pas remplie : de value et de otherValue, un seul des deux
     * est nul, l'autre etant non nul.
     */
    public static void checkMutualExclusion(String value, String fieldName,
                                            String otherValue, String otherFieldName) {
        if (value == null && otherValue == null ||
            value != null && otherValue != null) {
            LOGGER.info("Erreur metier : des deux champs [{}] (=[{}]) et [{}] (=[{}]), exactement un d'eux doit etre non nul",
                    fieldName, truncate(value), otherFieldName, truncate(otherValue));
            throw new ValidationException("Il faut fournir exactement un des deux champs suivants : \""
                    + fieldName + "\" et \"" + otherFieldName  + "\"");
        }
    }

    public static void checkSizeIdPrestation(String value) {
        final int MAX_SIZE = 50;
        checkSize(value, 1, MAX_SIZE, "idPrestation");
    }

    public static void checkSizeIdUsager(String value) {
        final int MAX_SIZE = 50;
        checkSize(value, 1, MAX_SIZE, "idUsager");
    }

    public static void checkSizeIdDemarcheSiMetier(String value) {
        final int MAX_SIZE = 50;
        checkSize(value, 1, MAX_SIZE, "idDemarcheSiMetier");
    }

    public static void checkSizeUrl(String value, String fieldName) {
        final int URL_MIN_LENGTH = 10;
        final int URL_MAX_LENGTH = 200;
        checkSize(value, URL_MIN_LENGTH, URL_MAX_LENGTH, fieldName);
    }

    public static void checkSizeDate(String value, String fieldName) {
        final int MIN_SIZE = 2;
        final int MAX_SIZE = 50;
        checkSize(value, MIN_SIZE, MAX_SIZE, fieldName);
    }

    /**
     * Verifie qu'une liste n'est pas vide.
     */
    public static void checkListNotEmpty(List list, String fieldName) {
        if (list != null && list.size() == 0) {
            LOGGER.info("Erreur metier : la liste [{}] est vide", fieldName);
            throw new EmptyListException(fieldName);
        }
    }

    /**
     * Verifie qu'une liste ne contient pas trop d'elements.
     */
    public static void checkListMaxSize(List list, String fieldName, int maxSize) {
        if (list != null && list.size() > maxSize) {
            LOGGER.info("Erreur metier : taille de liste [{}] = {}, taille max = {}", fieldName, list.size(), maxSize);
            throw new TooLargeListException(fieldName, list.size(), maxSize);
        }
    }

}
