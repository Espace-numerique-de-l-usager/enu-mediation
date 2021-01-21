package ch.ge.ael.enu.mediation.metier.validation;

import ch.ge.ael.enu.mediation.metier.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActionValidatorTest {

    @Test
    void check1() {
        new ActionValidator().validate(null, null, null, null);
    }

    @Test
    void check2() {
        assertThatThrownBy(() -> new ActionValidator().validate(null, null, null, "2023-02-18"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le champ \"dateEcheanceAction\" ne peut pas être fourni quand le champ \"libelleAction\" n'est pas fourni");
    }

}
