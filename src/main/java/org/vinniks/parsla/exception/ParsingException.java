package org.vinniks.parsla.exception;

public final class ParsingException extends ParslaException {
    private final Object position;

    public ParsingException(String message, Object position) {
        super(message + (position == null ? "" : " at " + position));
        this.position = position;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPosition() {
        return (T) position;
    }
}
