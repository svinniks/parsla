package org.vinniks.parsla.tokenizer;

import java.io.IOException;

public interface TokenIterator extends AutoCloseable {
    boolean hasNext() throws IOException;

    Token next() throws IOException;

    @Override
    default void close() {}
}
