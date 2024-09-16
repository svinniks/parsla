package org.vinniks.parsla.tokenizer.text;

import lombok.NonNull;
import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.TokenIterator;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public abstract class AbstractTextTokenIterator implements TokenIterator<TextPosition> {
    private final CharacterIterator characterIterator;

    private final Deque<Token> tokens;
    private final Deque<TextPosition> positions;
    private int line;
    private int column;

    private TextPosition position;

    protected AbstractTextTokenIterator(@NonNull CharacterIterator characterIterator) {
        this.characterIterator = characterIterator;
        tokens = new ArrayDeque<>();
        positions = new ArrayDeque<>();
        line = 1;
        column = 1;
    }

    protected final void push(Token token, TextPosition position) {
        tokens.push(token);
        positions.push(position);
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

        position = positions.pop();
        return tokens.pop();
    }

    @Override
    public TextPosition position() {
        return position;
    }

    @Override
    public void close() {
        characterIterator.close();
    }

    protected TextPosition characterPosition() {
        return new TextPosition(line, column);
    }

    private void ensureNextToken() throws IOException {
        if (tokens.isEmpty() && characterIterator.hasNext()) {
            while (tokens.isEmpty() && characterIterator.hasNext()) {
                var c = characterIterator.next();
                character(c);

                if (c == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
            }

            if (!characterIterator.hasNext()) {
                end();
            }
        }
    }
}
