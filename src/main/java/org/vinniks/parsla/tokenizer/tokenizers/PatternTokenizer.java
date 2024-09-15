package org.vinniks.parsla.tokenizer.tokenizers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.tokenizer.SimpleToken;
import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.TokenIterator;
import org.vinniks.parsla.tokenizer.TextTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PatternTokenizer implements TextTokenizer {
    private static final int BUFFER_SIZE = 8 * 1024;

    public static PatterTokenizerBuilder builder() {
        return new PatterTokenizerBuilder();
    }

    private final TokenPattern[] tokenPatterns;

    @Override
    public TokenIterator getTokenIterator(Reader source) throws IOException {
        return new PatternTokenIterator(readSource(source));
    }

    @Override
    public Iterable<String> getTokenTypes() {
        return () -> Arrays.stream(tokenPatterns).map(TokenPattern::getType).iterator();
    }

    private String readSource(Reader source) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        var builder = new StringBuilder();
        int charactersRead;

        while ((charactersRead = source.read(buffer, 0, buffer.length)) != -1) {
            builder.append(buffer, 0, charactersRead);
        }

        return builder.toString();
    }

    public class PatternTokenIterator implements TokenIterator {
        private final String source;
        private final Matcher[] matchers;
        private int position;
        private int line;
        private int column;

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private Optional<Token> nextToken;

        private PatternTokenIterator(String source) {
            this.source = source;

            matchers = Arrays
                .stream(tokenPatterns)
                .map(TokenPattern::getPattern)
                .map(pattern -> pattern.matcher(source))
                .toArray(Matcher[]::new);

            position = 0;
            line = 1;
            column = 1;
        }

        @SneakyThrows
        @Override
        public boolean hasNext() {
            ensureFirstToken();
            return nextToken.isPresent();
        }

        @SneakyThrows
        @Override
        public Token next() {
            ensureFirstToken();
            var token = nextToken.orElseThrow(NoSuchElementException::new);
            nextToken = getNextToken();

            return token;
        }

        private void ensureFirstToken() throws ParsingException {
            if (nextToken == null) {
                nextToken = getNextToken();
            }
        }

        private Optional<Token> getNextToken() throws ParsingException {
            if (position >= source.length()) {
                return Optional.empty();
            }

            String bestType = null;
            String bestValue = null;
            String bestCapturedValue = null;

            for (var i = 0; i < matchers.length; i++) {
                var matcher = matchers[i];
                matcher.region(position, source.length());

                if (matcher.find()) {
                    var value = matcher.group();
                    var capturedValue = matcher.groupCount() == 0 ? null : matcher.group(1);

                    if (bestType == null || value.length() > bestValue.length()) {
                        bestType = tokenPatterns[i].type;
                        bestValue = value;
                        bestCapturedValue = capturedValue;
                    }
                }
            }

            if (bestType == null) {
                throw new ParsingException(String.format("Unexpected input at line: %d, column: %d!", line, column));
            } else {
                calculatePosition(bestValue.length());
                return Optional.of(new SimpleToken(bestType, bestCapturedValue));
            }
        }

        private void calculatePosition(int valueLength) {
            for (int i = 0; i < valueLength; i++) {
                if (source.charAt(position++) == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class TokenPattern {
        private final String type;
        private final Pattern pattern;
    }

    public static class PatterTokenizerBuilder {
        private final Collection<TokenPattern> tokenPatterns;

        private PatterTokenizerBuilder() {
            tokenPatterns = new LinkedList<>();
        }

        public PatterTokenizerBuilder pattern(String name, String pattern) {
            tokenPatterns.add(new TokenPattern(name, Pattern.compile("^" + pattern, Pattern.DOTALL)));
            return this;
        }

        public PatternTokenizer build() {
            return new PatternTokenizer(tokenPatterns.toArray(TokenPattern[]::new));
        }
    }
}
