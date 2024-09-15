package org.vinniks.parsla.parser;

public interface ParserOutputListener {
    default void enter(String value) {}

    default void exit() {}

    default void tap(String value) {
        enter(value);
        exit();
    }
}
