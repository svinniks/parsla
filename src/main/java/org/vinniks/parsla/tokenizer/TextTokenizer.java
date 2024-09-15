package org.vinniks.parsla.tokenizer;

import java.io.IOException;
import java.io.Reader;

public interface TextTokenizer {
    TokenIterator getTokenIterator(Reader source) throws IOException;
    Iterable<String> getTokenTypes();
}
