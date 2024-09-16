package org.vinniks.parsla.tokenizer.text;

import org.vinniks.parsla.tokenizer.TokenIterator;

import java.io.IOException;
import java.io.Reader;

public interface TextTokenizer {
    TokenIterator<TextPosition> getTokenIterator(Reader source) throws IOException;
}
