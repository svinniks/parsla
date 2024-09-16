package org.vinniks.parsla.tokenizer;

import java.io.IOException;

public interface TokenIterator<P> extends AutoCloseable {
    boolean hasNext() throws IOException;

    Token next() throws IOException;

    P position();

    @Override
    default void close() {}
}
