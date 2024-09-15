package org.vinniks.parsla.grammar.serialization;

import lombok.RequiredArgsConstructor;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.tokenizer.SimpleToken;
import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.TokenIterator;
import org.vinniks.parsla.tokenizer.tokenizers.AbstractBufferedTextTokenIterator;
import org.vinniks.parsla.tokenizer.tokenizers.AbstractBufferedTextTokenizer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public final class GrammarTokenizer extends AbstractBufferedTextTokenizer {
    // Standard
    private static final String IDENTIFIER = "identifier";
    private static final String COLON = "colon";
    private static final String GT = "gt";
    private static final String LEFT_CURLY_BRACKET = "left-curly-bracket";
    private static final String RIGHT_CURLY_BRACKET = "right-curly-bracket";
    private static final String EXCLAMATION = "exclamation";
    private static final String COMMA = "comma";
    private static final String STRING = "string";
    private static final String SEMICOLON = "semicolon";
    private static final String EMPTY = "empty";

    private static final Token COLON_TOKEN = new SimpleToken(COLON);
    private static final Token GT_TOKEN = new SimpleToken(GT);
    private static final Token LEFT_CURLY_BRACKET_TOKEN = new SimpleToken(LEFT_CURLY_BRACKET);
    private static final Token RIGHT_CURLY_BRACKET_TOKEN = new SimpleToken(RIGHT_CURLY_BRACKET);
    private static final Token EXCLAMATION_TOKEN = new SimpleToken(EXCLAMATION);
    private static final Token COMMA_TOKEN = new SimpleToken(COMMA);
    private static final Token SEMICOLON_TOKEN = new SimpleToken(SEMICOLON);
    private static final Token EMPTY_TOKEN = new SimpleToken(EMPTY);

    // Extended
    private static final String LEFT_BRACKET = "left-bracket";
    private static final String RIGHT_BRACKET = "right-bracket";
    private static final String PLUS = "plus";
    private static final String ASTERISK = "asterisk";
    private static final String QUESTION_MARK = "question-mark";
    private static final String PIPE = "pipe";

    private static final Token LEFT_BRACKET_TOKEN = new SimpleToken(LEFT_BRACKET);
    private static final Token RIGHT_BRACKET_TOKEN = new SimpleToken(RIGHT_BRACKET);
    private static final Token PLUS_TOKEN = new SimpleToken(PLUS);
    private static final Token ASTERISK_TOKEN = new SimpleToken(ASTERISK);
    private static final Token QUESTION_MARK_TOKEN = new SimpleToken(QUESTION_MARK);
    private static final Token PIPE_TOKEN = new SimpleToken(PIPE);

    private static final Collection<String> STANDARD_TOKEN_TYPES = List.of(
        IDENTIFIER,
        COLON,
        GT,
        LEFT_CURLY_BRACKET,
        RIGHT_CURLY_BRACKET,
        COMMA,
        STRING,
        SEMICOLON,
        EMPTY
    );

    private static final Iterable<String> EXTENDED_TOKEN_TYPES = Stream
        .concat(
            STANDARD_TOKEN_TYPES.stream(),
            Stream.of(
                LEFT_BRACKET,
                RIGHT_BRACKET,
                PLUS,
                ASTERISK,
                QUESTION_MARK,
                PIPE
            )
        )
        .toList();

    private final boolean extended;

    @Override
    public Iterable<String> getTokenTypes() {
        return extended ? EXTENDED_TOKEN_TYPES : STANDARD_TOKEN_TYPES;
    }

    @Override
    protected TokenIterator getTokenIterator(CharacterIterator characterIterator) {
        return new GrammarTokenIterator(characterIterator, extended);
    }

    private static class GrammarTokenIterator extends AbstractBufferedTextTokenIterator {
        private enum State {
            LF_TOKEN,
            R_IDENTIFIER,
            R_STRING,
            LF_COMMENT_START,
            R_SHORT_COMMENT,
            R_LONG_COMMENT,
            LF_LONG_COMMENT_END
        }

        private final boolean extended;
        private State state;
        private final StringBuilder valueBuilder;

        private GrammarTokenIterator(CharacterIterator characterIterator, boolean extended) {
            super(characterIterator);
            this.extended = extended;
            state = State.LF_TOKEN;
            valueBuilder = new StringBuilder();
        }

        @Override
        protected void character(char c) {
            if (state == State.LF_TOKEN) {
                lfToken(c);
            } else if (state == State.R_IDENTIFIER) {
                rIdentifier(c);
            } else if (state == State.R_STRING) {
                rString(c);
            } else if (state == State.LF_COMMENT_START) {
                lfCommentStart(c);
            } else if (state == State.R_SHORT_COMMENT) {
                rShortComment(c);
            } else if (state == State.R_LONG_COMMENT) {
                rLongComment(c);
            } else if (state == State.LF_LONG_COMMENT_END) {
                lfLongCommentEnd(c);
            }
        }

        @Override
        protected void end() {
            if (state == State.R_IDENTIFIER) {
                push(new SimpleToken(IDENTIFIER, valueBuilder.toString()));
            } else if (state == State.R_LONG_COMMENT || state == State.LF_LONG_COMMENT_END || state == State.R_STRING) {
                throw new ParsingException("Unexpected end of the input");
            }
        }

        private void lfToken(char c) {
            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
                valueBuilder.setLength(0);
                valueBuilder.append(c);
                state = State.R_IDENTIFIER;
            } else if (c == ':') {
                push(COLON_TOKEN);
            } else if (c == '>') {
                push(GT_TOKEN);
            } else if (c == '{') {
                push(LEFT_CURLY_BRACKET_TOKEN);
            } else if (c == '}') {
                push(RIGHT_CURLY_BRACKET_TOKEN);
            } else if (c == '!') {
                push(EXCLAMATION_TOKEN);
            } else if (c == ',') {
                push(COMMA_TOKEN);
            } else if (c == '"') {
                valueBuilder.setLength(0);
                state = State.R_STRING;
            } else if (c == ';') {
                push(SEMICOLON_TOKEN);
            } else if (c == '^') {
                push(EMPTY_TOKEN);
            } else if (c == '/') {
                state = State.LF_COMMENT_START;
            } else if (extended) {
                lfExtendedToken(c);
            } else if (!Character.isWhitespace(c)) {
                throwUnexpectedCharacter(c);
            }
        }

        private void lfExtendedToken(char c) {
            if (c == '(') {
                push(LEFT_BRACKET_TOKEN);
            } else if (c == ')') {
                push(RIGHT_BRACKET_TOKEN);
            } else if (c == '+') {
                push(PLUS_TOKEN);
            } else if (c == '*') {
                push(ASTERISK_TOKEN);
            } else if (c == '?') {
                push(QUESTION_MARK_TOKEN);
            } else if (c == '|') {
                push(PIPE_TOKEN);
            } else if (!Character.isWhitespace(c)) {
                throwUnexpectedCharacter(c);
            }
        }

        private void rIdentifier(char c) {
            if (c == '-' || c == '_' || c >= '0' && c <= '9' || c >= 'A' && c < 'Z' || c >= 'a' && c <= 'z') {
                valueBuilder.append(c);
            } else {
                var value = valueBuilder.toString();
                state = State.LF_TOKEN;
                lfToken(c);
                push(new SimpleToken(IDENTIFIER, value));
            }
        }

        private void rString(char c) {
            if (c == '"') {
                push(new SimpleToken(STRING, valueBuilder.toString()));
                state = State.LF_TOKEN;
            } else {
                valueBuilder.append(c);
            }
        }

        private void lfCommentStart(char c) {
            if (c == '/') {
                state = State.R_SHORT_COMMENT;
            } else if (c == '*') {
                state = State.R_LONG_COMMENT;
            } else {
                throwUnexpectedCharacter(c);
            }
        }

        private void rShortComment(char c) {
            if (c == '\n') {
                state = State.LF_TOKEN;
            }
        }

        private void rLongComment(char c) {
            if (c == '*') {
                state = State.LF_LONG_COMMENT_END;
            }
        }

        private void lfLongCommentEnd(char c) {
            if (c == '/') {
                state = State.LF_TOKEN;
            } else {
                state = State.R_LONG_COMMENT;
            }
        }

        private void throwUnexpectedCharacter(char c) {
            throw new ParsingException(String.format("Unexpected character %s", c));
        }
    }
}
