package ch.ge.ael.enu.mediation.exception;

import ch.ge.ael.enu.mediation.business.exception.ValidationException;

public class IllegalMessageException extends ValidationException {

    public IllegalMessageException(String msg) {
        super(msg);
    }

}
