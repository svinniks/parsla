package org.vinniks.parsla.tokenizer.tokenizers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.TokenIterator;
import org.vinniks.parsla.tokenizer.TextTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

public abstract class AbstractBufferedTextTokenizer implements TextTokenizer {
    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
    public static final int DEFAULT_CACHED_BUFFER_COUNT = 128;

    private final int bufferSize;
    private final int cachedBufferCount;
    private final LinkedList<char[]> buffers;

    public AbstractBufferedTextTokenizer(int bufferSize, int cachedBufferCount) {
        this.bufferSize = bufferSize;
        this.cachedBufferCount = cachedBufferCount;
        buffers = new LinkedList<>();
    }

    public AbstractBufferedTextTokenizer() {
        this(DEFAULT_BUFFER_SIZE, DEFAULT_CACHED_BUFFER_COUNT);
    }

    @Override
    public abstract Iterable<String> getTokenTypes();

    protected abstract TokenIterator getTokenIterator(CharacterIterator characterIterator);

    @Override
    public TokenIterator getTokenIterator(Reader source) {
        return new TokenIteratorWrapper(source, acquireBuffer());
    }

    private char[] acquireBuffer() {
        synchronized (buffers) {
            if (!buffers.isEmpty()) {
                return buffers.pop();
            } else {
                return new char[bufferSize];
            }
        }
    }

    private void releaseBuffer(char[] buffer) {
        synchronized (buffers) {
            if (buffers.size() < cachedBufferCount) {
                buffers.push(buffer);
            }
        }
    }

    private class TokenIteratorWrapper implements TokenIterator {
        private final char[] buffer;
        private final TokenIterator tokenIterator;

        public TokenIteratorWrapper(Reader source, char[] buffer) {
            this.buffer = buffer;
            tokenIterator = getTokenIterator(new BufferedCharacterIterator(source, buffer));
        }

        @Override
        public boolean hasNext() throws IOException {
            return tokenIterator.hasNext();
        }

        @Override
        public Token next() throws IOException {
            return tokenIterator.next();
        }

        @Override
        public void close() {
            releaseBuffer(buffer);
        }
    }

    protected interface CharacterIterator {
        boolean hasNext() throws IOException;
        char next() throws IOException;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class BufferedCharacterIterator implements CharacterIterator {
        private final Reader source;
        private final char[] buffer;
        private int charactersRead = 0;
        private int i;

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

        private void ensureNext() throws IOException {
            if (charactersRead == 0 || charactersRead > 0 && i >= charactersRead) {
                charactersRead = source.read(buffer);
                i = 0;
            }
        }
    }
}
