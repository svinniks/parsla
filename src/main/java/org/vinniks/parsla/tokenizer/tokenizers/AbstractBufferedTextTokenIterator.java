package org.vinniks.parsla.tokenizer.tokenizers;

import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.TokenIterator;
import org.vinniks.parsla.tokenizer.tokenizers.AbstractBufferedTextTokenizer.CharacterIterator;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public abstract class AbstractBufferedTextTokenIterator implements TokenIterator {
    private final CharacterIterator characterIterator;
    private final Deque<Token> tokens;

    protected AbstractBufferedTextTokenIterator(CharacterIterator characterIterator) {
        this.characterIterator = characterIterator;
        tokens = new ArrayDeque<>();
    }

    protected final void push(Token token) {
        tokens.push(token);
    }

    protected abstract void character(char c);

    protected abstract void end();

    @Override
    public final boolean hasNext() throws IOException {
        ensureNextToken();
        return !tokens.isEmpty();
    }

    @Override
    public final Token next() throws IOException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return tokens.pop();
    }

    private void ensureNextToken() throws IOException {
        if (tokens.isEmpty() && characterIterator.hasNext()) {
            while (tokens.isEmpty() && characterIterator.hasNext()) {
                character(characterIterator.next());
            }

            if (!characterIterator.hasNext()) {
                end();
            }
        }
    }
}
