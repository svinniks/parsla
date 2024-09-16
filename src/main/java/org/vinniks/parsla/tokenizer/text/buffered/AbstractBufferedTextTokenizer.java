package org.vinniks.parsla.tokenizer.text.buffered;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.vinniks.parsla.tokenizer.TokenIterator;
import org.vinniks.parsla.tokenizer.text.AbstractTextTokenIterator;
import org.vinniks.parsla.tokenizer.text.CharacterIterator;
import org.vinniks.parsla.tokenizer.text.TextPosition;
import org.vinniks.parsla.tokenizer.text.TextTokenizer;

import java.io.Reader;

@RequiredArgsConstructor
public abstract class AbstractBufferedTextTokenizer implements TextTokenizer {
    @NonNull
    private final CharacterBufferProvider characterBufferProvider;

    protected abstract AbstractTextTokenIterator getTokenIterator(CharacterIterator characterIterator);

    @Override
    public TokenIterator<TextPosition> getTokenIterator(Reader source) {
        var characterIterator = new BufferedCharacterIterator(source, characterBufferProvider);
        return getTokenIterator(characterIterator);
    }
}
