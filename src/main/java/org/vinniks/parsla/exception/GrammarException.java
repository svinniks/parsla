package org.vinniks.parsla.exception;

public final class GrammarException extends ParslaException {
    public GrammarException(String message) {
        super(message);
    }

    public GrammarException(String message, Throwable cause) {
        super(message, cause);
    }
}
