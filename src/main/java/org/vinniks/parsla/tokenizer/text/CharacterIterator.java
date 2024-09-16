package org.vinniks.parsla.tokenizer.text;

import java.io.IOException;

public interface CharacterIterator extends AutoCloseable {
    boolean hasNext() throws IOException;

    char next() throws IOException;

    @Override
    default void close() {}
}
