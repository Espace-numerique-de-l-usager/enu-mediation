package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.IllegalEnumValueException;
import ch.ge.ael.enu.mediation.metier.exception.IllegalStringSizeException;
import ch.ge.ael.enu.mediation.metier.exception.MalformedDateException;
import ch.ge.ael.enu.mediation.metier.exception.MissingFieldException;
import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import ch.ge.ael.enu.mediation.metier.model.DemarcheStatus;
import ch.ge.ael.enu.mediation.routes.MediaType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationUtilsTest {

    @Test
    void checkExistence_of_non_null_value_should_succeed() {
        ValidationUtils.checkExistence("someValue", "someField");
    }

    @Test
    void checkExistence_of_null_value_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkExistence(null, "someField"))
                .isInstanceOf(MissingFieldException.class)
                .hasMessage("Le champ \"someField\" manque");
    }

    @Test
    void checkEnum_with_correct_value_should_succeed() {
        ValidationUtils.checkEnum("DEPOSEE", DemarcheStatus.class, "someEnumField");
        ValidationUtils.checkEnum(null, DemarcheStatus.class, "someEnumField");
    }

    @Test
    void checkEnum_with_incorrect_value_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkEnum("WRONG_VALUE", DemarcheStatus.class, "someEnumField"))
                .isInstanceOf(IllegalEnumValueException.class)
                .hasMessage("La valeur \"WRONG_VALUE\" du champ \"someEnumField\" est incorrecte. Les valeurs possibles sont : [BROUILLON, DEPOSEE, EN_TRAITEMENT, TERMINEE]");
    }

    @Test
    void checkEnum_with_non_enum_class_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkEnum("ANY_VALUE", MediaType.class, "someEnumField"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void checkSize_with_correct_value_should_succeed() {
        ValidationUtils.checkSize("pipo1", 2, 10, "someField");
        ValidationUtils.checkSize("pipo2", 4, 10, "someField");
        ValidationUtils.checkSize("pipo3", 1, 5, "someField");
        ValidationUtils.checkSize(null, 2, 10, "someField");
    }

    @Test
    void checkSize_with_too_small_value_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkSize("tooShort", 20, 30, "someField"))
                .isInstanceOf(IllegalStringSizeException.class)
                .hasMessage("La valeur \"tooShort\" du champ \"someField\" est d'une taille incorrecte. Taille autorisee : entre 20 et 30 caracteres");
        assertThatThrownBy(() -> ValidationUtils.checkSize("   abc   ", 4, 30, "someField"))
                .isInstanceOf(IllegalStringSizeException.class)
                .hasMessage("La valeur \"abc\" du champ \"someField\" est d'une taille incorrecte. Taille autorisee : entre 4 et 30 caracteres");
    }

    @Test
    void checkSize_with_too_large_value_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkSize("tooLong", 1, 5, "someField"))
                .isInstanceOf(IllegalStringSizeException.class)
                .hasMessage("La valeur \"tooLong\" du champ \"someField\" est d'une taille incorrecte. Taille autorisee : entre 1 et 5 caracteres");
    }

    @Test
    void checkDate_with_correct_date_should_succeed() {
        ValidationUtils.checkDate("2021-02-18", "someDateField");
        ValidationUtils.checkDate(null, "someDateField");
    }

    @Test
    void checkDate_with_wrong_date_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkDate("2021", "someDateField1"))
                .isInstanceOf(MalformedDateException.class)
                .hasMessage("La valeur \"2021\" du champ \"someDateField1\" est incorrecte. Le format attendu est \"yyyy-MM-dd\"");
        assertThatThrownBy(() -> ValidationUtils.checkDate("2021_02_18", "someDateField2"))
                .isInstanceOf(MalformedDateException.class)
                .hasMessage("La valeur \"2021_02_18\" du champ \"someDateField2\" est incorrecte. Le format attendu est \"yyyy-MM-dd\"");
        assertThatThrownBy(() -> ValidationUtils.checkDate("2021-02-18T09:00:00", "someDateField3"))
                .isInstanceOf(MalformedDateException.class)
                .hasMessage("La valeur \"2021-02-18T09:00:00\" du champ \"someDateField3\" est incorrecte. Le format attendu est \"yyyy-MM-dd\"");
    }

    @Test
    void checkSizeUrl_with_correct_url_should_succeed() {
        ValidationUtils.checkSizeUrl(null, "someUrlField");
        ValidationUtils.checkSizeUrl("https://www.pcp.pt", "someUrlField");
        ValidationUtils.checkSizeUrl("Une URL un peu bizarre", "someUrlField");
    }

    @Test
    void checkSizeUrl_with_wrong_url_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkSizeUrl("short", "someUrlField1"))
                .isInstanceOf(IllegalStringSizeException.class)
                .hasMessage("La valeur \"short\" du champ \"someUrlField1\" est d'une taille incorrecte. Taille autorisee : entre 10 et 200 caracteres");

        String longUrl = "https://someSite/path?" +
                " 123456789 123456789 123456789 123456789 123456789" +
                " 123456789 123456789 123456789 123456789 123456789" +
                " 123456789 123456789 123456789 123456789 123456789" +
                " 123456789 123456789 123456789 123456789 123456789" +
                " 123456789 123456789 123456789 123456789 123456789" +
                " 123456789 123456789 123456789 123456789 123456789";
        assertThatThrownBy(() -> ValidationUtils.checkSizeUrl(longUrl, "someUrlField2"))
                .isInstanceOf(IllegalStringSizeException.class)
                .hasMessage("La valeur \"" + longUrl + "\" du champ \"someUrlField2\" est d'une taille incorrecte. Taille autorisee : entre 10 et 200 caracteres");
    }

    @Test
    void checkAbsentIfOtherAbsent_with_absent_absent_should_succeed() {
        ValidationUtils.checkAbsentIfOtherAbsent(null, "someField", null, "someOtherField");
    }

    @Test
    void checkAbsentIfOtherAbsent_with_present_present_should_succeed() {
        ValidationUtils.checkAbsentIfOtherAbsent("someValue", "someField", "someOtherValue", "someOtherField");
    }

    @Test
    void checkAbsentIfOtherAbsent_with_absent_present_should_succeed() {
        ValidationUtils.checkAbsentIfOtherAbsent(null, "someField", "someOtherValue", "someOtherField");
    }

    @Test
    void checkAbsentIfOtherAbsent_with_present_absent_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkAbsentIfOtherAbsent("someValue", "someField", null, "someOtherField"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le champ \"someField\" ne peut pas être fourni quand le champ \"someOtherField\" n'est pas fourni");
    }

    @Test
    void checkPresentIfOtherPresent_with_present_present_should_succeed() {
        ValidationUtils.checkPresentIfOtherPresent("someValue", "someField", "someOtherValue", "someOtherField");
    }

    @Test
    void checkPresentIfOtherPresent_with_absent_absent_should_succeed() {
        ValidationUtils.checkPresentIfOtherPresent(null, "someField", null, "someOtherField");
    }

    @Test
    void checkPresentIfOtherPresent_with_present_absent_should_succeed() {
        ValidationUtils.checkPresentIfOtherPresent("someValue", "someField", null, "someOtherField");
    }

    @Test
    void checkPresentIfOtherPresent_with_absent_present_should_fail() {
        assertThatThrownBy(() -> ValidationUtils.checkPresentIfOtherPresent(null, "someField", "someOtherValue", "someOtherField"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le champ \"someField\" doit être fourni quand le champ \"someOtherField\" est fourni");
    }

}
