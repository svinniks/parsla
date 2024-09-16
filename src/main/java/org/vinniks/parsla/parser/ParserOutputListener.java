package org.vinniks.parsla.parser;

public interface ParserOutputListener<P> {
    default void enter(P position, String value) {}

    default void exit() {}

    default void tap(P position, String value) {
        enter(position, value);
        exit();
    }
}
