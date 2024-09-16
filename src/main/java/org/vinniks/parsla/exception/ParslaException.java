package org.vinniks.parsla.exception;

public abstract sealed class ParslaException extends RuntimeException permits ParsingException, GrammarException, SyntaxTreeException {
    public ParslaException(String message) {
        super(message);
    }

    public ParslaException(String message, Throwable cause) {
        super(message, cause);
    }
}
