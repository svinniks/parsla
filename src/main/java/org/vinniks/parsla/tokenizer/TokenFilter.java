package org.vinniks.parsla.tokenizer;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class TokenFilter implements TextTokenizer {
    public static TokenFilter ignoreTokens(TextTokenizer tokenizer, String... tokenTypes) {
        var tokenTypeSet = new HashSet<String>(tokenTypes.length);
        Collections.addAll(tokenTypeSet, tokenTypes);
        return new TokenFilter(tokenizer, token -> !tokenTypeSet.contains(token.getType()));
    }

    @NonNull
    private final TextTokenizer tokenizer;

    @NonNull
    private final Predicate<Token> predicate;

    @Override
    public TokenIterator getTokenIterator(Reader source) throws IOException {
        return new FilteredTokenIterator(tokenizer.getTokenIterator(source));
    }

    @Override
    public Iterable<String> getTokenTypes() {
        return tokenizer.getTokenTypes();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class FilteredTokenIterator implements TokenIterator {
        private final TokenIterator tokenIterator;
        private Token nextToken;

        @Override
        public boolean hasNext() throws IOException {
            ensureNextToken();
            return nextToken != null;
        }

        @Override
        public Token next() throws IOException {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            var token = nextToken;
            nextToken = null;
            return token;
        }

        @Override
        public void close() {
            tokenIterator.close();
        }

        private void ensureNextToken() throws IOException {
            while (nextToken == null && tokenIterator.hasNext()) {
                var token = tokenIterator.next();

                if (predicate.test(token)) {
                    nextToken = token;
                }
            }
        }
    }
}
