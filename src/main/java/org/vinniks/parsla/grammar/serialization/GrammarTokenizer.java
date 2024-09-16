package org.vinniks.parsla.grammar.serialization;

import lombok.Getter;
import lombok.NonNull;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.text.AbstractTextTokenIterator;
import org.vinniks.parsla.tokenizer.text.CharacterIterator;
import org.vinniks.parsla.tokenizer.text.TextPosition;
import org.vinniks.parsla.tokenizer.text.buffered.AbstractBufferedTextTokenizer;
import org.vinniks.parsla.tokenizer.text.buffered.CharacterBufferProvider;

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
    private static final String CARET = "caret";

    private static final Token COLON_TOKEN = new Token(COLON);
    private static final Token GT_TOKEN = new Token(GT);
    private static final Token LEFT_CURLY_BRACKET_TOKEN = new Token(LEFT_CURLY_BRACKET);
    private static final Token RIGHT_CURLY_BRACKET_TOKEN = new Token(RIGHT_CURLY_BRACKET);
    private static final Token EXCLAMATION_TOKEN = new Token(EXCLAMATION);
    private static final Token COMMA_TOKEN = new Token(COMMA);
    private static final Token SEMICOLON_TOKEN = new Token(SEMICOLON);
    private static final Token CARET_TOKEN = new Token(CARET);

    // Extended
    private static final String LEFT_BRACKET = "left-bracket";
    private static final String RIGHT_BRACKET = "right-bracket";
    private static final String PLUS = "plus";
    private static final String ASTERISK = "asterisk";
    private static final String QUESTION = "question";
    private static final String PIPE = "pipe";

    private static final Token LEFT_BRACKET_TOKEN = new Token(LEFT_BRACKET);
    private static final Token RIGHT_BRACKET_TOKEN = new Token(RIGHT_BRACKET);
    private static final Token PLUS_TOKEN = new Token(PLUS);
    private static final Token ASTERISK_TOKEN = new Token(ASTERISK);
    private static final Token QUESTION_MARK_TOKEN = new Token(QUESTION);
    private static final Token PIPE_TOKEN = new Token(PIPE);

    @Getter
    private final boolean extended;

    private final IdentifierCharacterValidator identifierCharacterValidator;

    GrammarTokenizer(
        boolean extended,
        @NonNull IdentifierCharacterValidator identifierCharacterValidator,
        CharacterBufferProvider characterBufferProvider
    ) {
        super(characterBufferProvider);
        this.extended = extended;
        this.identifierCharacterValidator = identifierCharacterValidator;
    }

    @Override
    public AbstractTextTokenIterator getTokenIterator(CharacterIterator characterIterator) {
        return new GrammarTokenIterator(characterIterator, extended);
    }

    private class GrammarTokenIterator extends AbstractTextTokenIterator {
        private enum State {
            LF_TOKEN,
            R_IDENTIFIER,
            R_STRING,
            LF_COMMENT_START,
            R_SHORT_COMMENT,
            R_LONG_COMMENT,
            LF_LONG_COMMENT_END,
            LF_ESCAPED_CHARACTER,
            R_UNICODE_CHARACTER_CODE
        }

        private final boolean extended;
        private State state;
        private final StringBuilder valueBuilder;
        private final StringBuilder characterCodeBuilder;
        private TextPosition tokenPosition;

        private GrammarTokenIterator(CharacterIterator characterIterator, boolean extended) {
            super(characterIterator);
            this.extended = extended;
            state = State.LF_TOKEN;
            valueBuilder = new StringBuilder();
            characterCodeBuilder = new StringBuilder();
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
            } else if (state == State.LF_ESCAPED_CHARACTER) {
                lfEscapedCharacter(c);
            } else  {
                //State.R_UNICODE_CHARACTER_CODE
                rUnicodeCharacterCode(c);
            }
        }

        @Override
        protected void end() {
            if (state == State.R_IDENTIFIER) {
                push(new Token(IDENTIFIER, valueBuilder.toString()), tokenPosition);
            } else if (
                state == State.R_LONG_COMMENT
                || state == State.LF_COMMENT_START
                || state == State.LF_LONG_COMMENT_END
                || state == State.R_STRING
                || state == State.R_UNICODE_CHARACTER_CODE
            ) {
                throw new ParsingException("unexpected end of the input", characterPosition());
            }
        }

        private void lfToken(char c) {
            if (isValidFirstIdentifierCharacter(c)) {
                valueBuilder.setLength(0);
                valueBuilder.append(c);
                state = State.R_IDENTIFIER;
                tokenPosition = characterPosition();
            } else if (c == ':') {
                push(COLON_TOKEN, characterPosition());
            } else if (c == '>') {
                push(GT_TOKEN, characterPosition());
            } else if (c == '{') {
                push(LEFT_CURLY_BRACKET_TOKEN, characterPosition());
            } else if (c == '}') {
                push(RIGHT_CURLY_BRACKET_TOKEN, characterPosition());
            } else if (c == '!') {
                push(EXCLAMATION_TOKEN, characterPosition());
            } else if (c == ',') {
                push(COMMA_TOKEN, characterPosition());
            } else if (c == '"') {
                valueBuilder.setLength(0);
                state = State.R_STRING;
                tokenPosition = characterPosition();
            } else if (c == ';') {
                push(SEMICOLON_TOKEN, characterPosition());
            } else if (c == '^') {
                push(CARET_TOKEN, characterPosition());
            } else if (c == '/') {
                state = State.LF_COMMENT_START;
            } else if (extended) {
                lfExtendedToken(c);
            } else if (!Character.isWhitespace(c)) {
                throw unexpectedCharacter(c);
            }
        }

        private void lfExtendedToken(char c) {
            if (c == '(') {
                push(LEFT_BRACKET_TOKEN, characterPosition());
            } else if (c == ')') {
                push(RIGHT_BRACKET_TOKEN, characterPosition());
            } else if (c == '+') {
                push(PLUS_TOKEN, characterPosition());
            } else if (c == '*') {
                push(ASTERISK_TOKEN, characterPosition());
            } else if (c == '?') {
                push(QUESTION_MARK_TOKEN, characterPosition());
            } else if (c == '|') {
                push(PIPE_TOKEN, characterPosition());
            } else if (!Character.isWhitespace(c)) {
                throw unexpectedCharacter(c);
            }
        }

        private void rIdentifier(char c) {
            if (isValidNextIdentifierCharacter(c)) {
                valueBuilder.append(c);
            } else {
                var value = valueBuilder.toString();
                var position = tokenPosition;
                state = State.LF_TOKEN;
                lfToken(c);
                push(new Token(IDENTIFIER, value), position);
            }
        }

        private void rString(char c) {
            if (c == '"') {
                push(new Token(STRING, valueBuilder.toString()), tokenPosition);
                state = State.LF_TOKEN;
            } else if (c == '\\') {
                state = State.LF_ESCAPED_CHARACTER;
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
                throw unexpectedCharacter(c);
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

        private void lfEscapedCharacter(char c) {
            if (c == 't') {
                valueBuilder.append('\t');
                state = State.R_STRING;
            } else if (c == 'b') {
                valueBuilder.append('\b');
                state = State.R_STRING;
            } else if (c == 'n') {
                valueBuilder.append('\n');
                state = State.R_STRING;
            } else if (c == 'r') {
                valueBuilder.append('\r');
                state = State.R_STRING;
            } else if (c == 'f') {
                valueBuilder.append('\f');
                state = State.R_STRING;
            } else if (c == '\'') {
                valueBuilder.append('\'');
                state = State.R_STRING;
            } else if (c == '"') {
                valueBuilder.append('"');
                state = State.R_STRING;
            } else if (c == '\\') {
                valueBuilder.append('\\');
                state = State.R_STRING;
            } else if (c == 'u') {
                characterCodeBuilder.setLength(0);
                state = State.R_UNICODE_CHARACTER_CODE;
            } else {
                throw new ParsingException(String.format("invalid escape character %s", c), characterPosition());
            }
        }

        private void rUnicodeCharacterCode(char c) {
            if (c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f' || c >= '0' && c <= '9') {
                characterCodeBuilder.append(c);

                if (characterCodeBuilder.length() == 4) {
                    valueBuilder.append((char) Integer.parseInt(characterCodeBuilder.toString(), 16));
                    state = State.R_STRING;
                }
            } else {
                throw new ParsingException(String.format("invalid hexadecimal digit %s in character code", c), characterPosition());
            }
        }

        private boolean isValidFirstIdentifierCharacter(char c) {
            return identifierCharacterValidator.isValidFirstCharacter(c) && !isReservedCharacter(c);
        }

        private boolean isValidNextIdentifierCharacter(char c) {
            return identifierCharacterValidator.isValidNextCharacter(c) && !isReservedCharacter(c);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean isReservedCharacter(char c) {
            return Character.isWhitespace(c) || c == '{' || c == '}' || c == '"' || c == '>' || c == ',' || c == ';' || c == ':' || c == '!'
                || extended && (c == '(' || c == ')' || c == '?' || c == '+' || c == '*' || c == '|');
        }

        private ParsingException unexpectedCharacter(char c) {
            return new ParsingException(String.format("unexpected character %s", c), characterPosition());
        }
    }
}
