package org.vinniks.parsla.tokenizer.text.buffered;

import lombok.NonNull;
import org.vinniks.parsla.tokenizer.text.CharacterIterator;

import java.io.IOException;
import java.io.Reader;

public class BufferedCharacterIterator implements CharacterIterator {
    private final Reader source;
    private final CharacterBufferProvider characterBufferProvider;

    private final char[] buffer;
    private int charactersRead = 0;
    private int i;

    public BufferedCharacterIterator(@NonNull Reader source, @NonNull CharacterBufferProvider characterBufferProvider) {
        this.source = source;
        this.characterBufferProvider = characterBufferProvider;
        buffer = characterBufferProvider.acquireBuffer();

        if (buffer.length == 0) {
            throw new IllegalArgumentException("buffer size can not be 0");
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        ensureNext();
        return charactersRead != -1;
    }

    @Override
    public char next() throws IOException {
        ensureNext();
        return buffer[i++];
    }

    @Override
    public void close() {
        characterBufferProvider.releaseBuffer(buffer);
    }

    private void ensureNext() throws IOException {
        if (charactersRead == 0 || charactersRead > 0 && i >= charactersRead) {
            charactersRead = source.read(buffer);
            i = 0;
        }
    }
}
