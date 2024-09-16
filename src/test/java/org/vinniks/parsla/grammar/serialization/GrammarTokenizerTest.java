package org.vinniks.parsla.grammar.serialization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.TokenIterator;
import org.vinniks.parsla.tokenizer.text.TextPosition;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GrammarTokenizerTest {
    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileCreatingGrammarTokenizerWithNullIdentifierCharacterValidator() {
        assertThatThrownBy(() -> new GrammarTokenizer(false, null, () -> new char[10]))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("identifierCharacterValidator is marked non-null but is null");
    }

    @Test
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileCreatingGrammarTokenizerWithNullCharacterBufferProvider() {
        assertThatThrownBy(() -> new GrammarTokenizer(false, new StandardIdentifierCharacterValidator(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("characterBufferProvider is marked non-null but is null");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldSuccessfullyCreateStandardGrammarTokenizer(boolean extended) {
        var tokenizer = new GrammarTokenizer(extended, new StandardIdentifierCharacterValidator(), () -> new char[10]);

        assertThat(tokenizer.isExtended()).isEqualTo(extended);
    }

    @Test
    void shouldThrowNullPointerExceptionWithCorrectMessageWhenGettingTokenIteratorForNullReader() {
        var tokenizer = new GrammarTokenizer(false, new StandardIdentifierCharacterValidator(), () -> new char[10]);

        assertThatThrownBy(() -> tokenizer.getTokenIterator((Reader)null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("source is marked non-null but is null");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldEmitNoTokensWhenEmptySource(boolean extended) throws IOException {
        var source = "";

        try (var tokens = initializeTokenIterator(extended, source)) {
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldEmitNoTokensWhenSourceConsistsOfOnlySpaces(boolean extended) throws IOException {
        var source = " \t\n\r ";

        try (var tokens = initializeTokenIterator(extended, source)) {
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldEmitAllSupportedStandardSingleCharacterTokens(boolean extended) throws IOException {
        var source = ":>{}!,;^";

        try (var tokens = initializeTokenIterator(extended, source)) {
            assertToken(tokens.next(), "colon", null);
            assertToken(tokens.next(), "gt", null);
            assertToken(tokens.next(), "left-curly-bracket", null);
            assertToken(tokens.next(), "right-curly-bracket", null);
            assertToken(tokens.next(), "exclamation", null);
            assertToken(tokens.next(), "comma", null);
            assertToken(tokens.next(), "semicolon", null);
            assertToken(tokens.next(), "caret", null);
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"(", ")", "+", "*", "?", "|"})
    void shouldFailOnAllExtendedSingleCharacterTokensWhenUsingStandardTokenizer(String token) throws IOException {
        var source = "{}" + token;

        try (var tokens = initializeTokenIterator(false, source)) {
            tokens.next();
            tokens.next();

            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("unexpected character " + token + " at 1:3");
        }
    }

    @Test
    void shouldEmitAllSupportedSingleCharacterTokensWhenUsingExtendedTokenizer() throws IOException {
        var source = ":>{}!,;^()+*?|";

        try (var tokens = initializeTokenIterator(true, source)) {
            assertToken(tokens.next(), "colon", null);
            assertToken(tokens.next(), "gt", null);
            assertToken(tokens.next(), "left-curly-bracket", null);
            assertToken(tokens.next(), "right-curly-bracket", null);
            assertToken(tokens.next(), "exclamation", null);
            assertToken(tokens.next(), "comma", null);
            assertToken(tokens.next(), "semicolon", null);
            assertToken(tokens.next(), "caret", null);
            assertToken(tokens.next(), "left-bracket", null);
            assertToken(tokens.next(), "right-bracket", null);
            assertToken(tokens.next(), "plus", null);
            assertToken(tokens.next(), "asterisk", null);
            assertToken(tokens.next(), "question", null);
            assertToken(tokens.next(), "pipe", null);
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldThrowParsingExceptionOnUnexpectedCharacterWhenUsingStandardTokenizerAtTheBeginningOfTheSource() {
        var source = "#";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("unexpected character # at 1:1");
        }
    }

    @Test
    void shouldThrowParsingExceptionOnUnexpectedCharacterWhenUsingStandardTokenizerAfterSomeSpacesInTheSource() {
        var source = "  \t#";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("unexpected character # at 1:4");
        }
    }

    @Test
    void shouldThrowParsingExceptionOnUnexpectedCharacterWhenUsingExtendedTokenizerAtTheBeginningOfTheSource() {
        var source = "#";

        try (var tokens = initializeTokenIterator(true, source)) {
            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("unexpected character # at 1:1");
        }
    }

    @Test
    void shouldThrowParsingExceptionOnUnexpectedCharacterWhenUsingExtendedTokenizerAfterSomeSpacesInTheSource() {
        var source = "  \t#";

        try (var tokens = initializeTokenIterator(true, source)) {
            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("unexpected character # at 1:4");
        }
    }

    @Test
    void shouldNotEmitAnyTokensWhenUsingStandardTokenizerWithOnlySpacesInTheSource() throws IOException {
        var source = "   \n\r\t   \n   ";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldNotEmitAnyTokensWhenUsingExtendedTokenizerWithOnlySpacesInTheSource() throws IOException {
        var source = "   \n\r\t   \n   ";

        try (var tokens = initializeTokenIterator(true, source)) {
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @MethodSource("provideIdentifierAllowedFirstCharacters")
    void shouldEmitIdentifierTokenWhenUsingStandardTokenizerWithAllowedIdentifierFirstCharacter(char letter) throws IOException {
        var source = String.valueOf(letter);

        try (var tokens = initializeTokenIterator(false, source)) {
            assertToken(tokens.next(), "identifier", source);
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @MethodSource("provideIdentifierAllowedFirstCharacters")
    void shouldEmitIdentifierTokenWhenUsingExtendedTokenizerWithAllowedIdentifierFirstCharacter(char letter) throws IOException {
        var source = String.valueOf(letter);

        try (var tokens = initializeTokenIterator(true, source)) {
            assertToken(tokens.next(), "identifier", source);
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @MethodSource("provideIdentifierAllowedNextCharacters")
    void shouldEmitIdentifierTokenWhenUsingStandardTokenizerWithAllowedIdentifierNextCharacter(char letter) throws IOException {
        var source = "A" + letter + letter;

        try (var tokens = initializeTokenIterator(false, source)) {
            assertToken(tokens.next(), "identifier", source);
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @MethodSource("provideIdentifierAllowedNextCharacters")
    void shouldEmitIdentifierTokenWhenUsingExtendedTokenizerWithAllowedIdentifierNextCharacter(char letter) throws IOException {
        var source = "A" + letter + letter;

        try (var tokens = initializeTokenIterator(true, source)) {
            assertToken(tokens.next(), "identifier", source);
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldEmitIdentifierAndOneCharacterTokenWhenUsingStandardTokenizer() throws IOException {
        var source = "abc:";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertToken(tokens.next(), "identifier", "abc");
            assertToken(tokens.next(), "colon", null);
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldEmitIdentifierAndOneCharacterTokenWhenUsingExtendedTokenizer() throws IOException {
        var source = "abc?";

        try (var tokens = initializeTokenIterator(true, source)) {
            assertToken(tokens.next(), "identifier", "abc");
            assertToken(tokens.next(), "question", null);
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldReportCorrectIdentifierPositionWhenStringLiteralStartingRightAfterIdentifier(boolean extended) throws IOException {
        var source = "abc\"H";

        try (var tokens = initializeTokenIterator(extended, source)) {
            assertToken(tokens.next(), "identifier", "abc");
            assertPosition(tokens.position(), 1, 1);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldEmitEmptyStringLiteral(boolean extended) throws IOException {
        var source = "\"\"";

        try (var tokens = initializeTokenIterator(extended, source)) {
            assertToken(tokens.next(), "string", "");
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldEmitNonEmptyStringLiteral(boolean extended) throws IOException {
        var source = "\"Hello, World!\"";

        try (var tokens = initializeTokenIterator(extended, source)) {
            assertToken(tokens.next(), "string", "Hello, World!");
        }
    }

    @Test
    void shouldThrowParsingExceptionOnInvalidEscapedCharacter() {
        var source = "\"\\h\"";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("invalid escape character h at 1:3");
            }
    }

    @ParameterizedTest
    @MethodSource("provideEscapedAndExpectedCharacters")
    void shouldEmitStringLiteralWithEscapedCharacter(String escapedCharacter, char expectedCharacter) throws IOException {
        var source = String.format("\"%s\"", escapedCharacter);

        try (var tokens = initializeTokenIterator(false, source)) {
            assertToken(tokens.next(), "string", String.valueOf(expectedCharacter));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"x", "0x", "00x", "000x"})
    void shouldThrowParsingExceptionWhenInvalidHexadecimalDigitUsedInUnicodeCharacterCode(String characterCodeWithInvalidDigit) {
        var source = String.format("\"\\u%s\"", characterCodeWithInvalidDigit);

        try (var tokens = initializeTokenIterator(false, source)) {
            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("invalid hexadecimal digit x in character code at 1:" + (characterCodeWithInvalidDigit.length() + 3));
        }
    }

    @ParameterizedTest
    @MethodSource("provideHexadecimalDigits")
    void shouldEmitStringLiteralWithCorrectUnicodeCharacterCode(char digit) throws IOException {
        var source = String.format("\"\\u%s%s%s%s\"", digit, digit, digit, digit);

        try (var tokens = initializeTokenIterator(false, source)) {
            assertToken(
                tokens.next(),
                "string",
                String.valueOf((char) Integer.parseInt("" + digit + digit + digit + digit, 16))
            );
        }
    }

    @Test
    void shouldThrowParsingExceptionWhenInvalidCommentStart() {
        var source = "/v";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("unexpected character v at 1:2");
        }
    }

    @Test
    void shouldNotEmitAnyTokensIfSourceContainsOnlyShortComment() throws IOException {
        var source = "//hello";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldEmitTokenAfterShortCommentAndNewLine() throws IOException {
        var source = "//hello\nabc";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertToken(tokens.next(), "identifier", "abc");
        }
    }

    @Test
    void shouldEmitIdentifierFollowedByShortComment() throws IOException {
        var source = "abc//hello";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertToken(tokens.next(), "identifier", "abc");
        }
    }

    @Test
    void shouldNotEmitAnyTokensIfSourceContainsOnlyLongCommentWithAsterisk() throws IOException {
        var source = "/* hello * world */";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/* hello", // unfinished long comment
        "/", // unfinished comment start
        "/* hello *", // unfinished long comment end
        "\"hello", // unfinished string literal
        "\"\\u00" // unfinished unicode character code
    })
    void shouldThrowParsingExceptionWhenUnexpectedEndOfTheInput(String source) {
        try (var tokens = initializeTokenIterator(false, source)) {
            assertThatThrownBy(tokens::next)
                .isInstanceOf(ParsingException.class)
                .hasMessage("unexpected end of the input at 1:" + (source.length() + 1));
        }
    }

    @Test
    void shouldEmitAllTokensWithCorrectPositionsWhileReadingComplexSource() throws IOException {
        var source = """
            option-1: {token-type-1, >"token-value-1"};
            
            >option-2: ^;
            
            option-3: option-1 >option-2;""";

        try (var tokens = initializeTokenIterator(false, source)) {
            assertToken(tokens.next(), "identifier", "option-1");
            assertPosition(tokens.position(), 1, 1);

            assertToken(tokens.next(), "colon", null);
            assertPosition(tokens.position(), 1, 9);

            assertToken(tokens.next(), "left-curly-bracket", null);
            assertPosition(tokens.position(), 1, 11);

            assertToken(tokens.next(), "identifier", "token-type-1");
            assertPosition(tokens.position(), 1, 12);

            assertToken(tokens.next(), "comma", null);
            assertPosition(tokens.position(), 1, 24);

            assertToken(tokens.next(), "gt", null);
            assertPosition(tokens.position(), 1, 26);

            assertToken(tokens.next(), "string", "token-value-1");
            assertPosition(tokens.position(), 1, 27);

            assertToken(tokens.next(), "right-curly-bracket", null);
            assertPosition(tokens.position(), 1, 42);

            assertToken(tokens.next(), "semicolon", null);
            assertPosition(tokens.position(), 1, 43);

            assertToken(tokens.next(), "gt", null);
            assertPosition(tokens.position(), 3, 1);

            assertToken(tokens.next(), "identifier", "option-2");
            assertPosition(tokens.position(), 3, 2);

            assertToken(tokens.next(), "colon", null);
            assertPosition(tokens.position(), 3, 10);

            assertToken(tokens.next(), "caret", null);
            assertPosition(tokens.position(), 3, 12);

            assertToken(tokens.next(), "semicolon", null);
            assertPosition(tokens.position(), 3, 13);

            assertToken(tokens.next(), "identifier", "option-3");
            assertPosition(tokens.position(), 5, 1);

            assertToken(tokens.next(), "colon", null);
            assertPosition(tokens.position(), 5, 9);

            assertToken(tokens.next(), "identifier", "option-1");
            assertPosition(tokens.position(), 5, 11);

            assertToken(tokens.next(), "gt", null);
            assertPosition(tokens.position(), 5, 20);

            assertToken(tokens.next(), "identifier", "option-2");
            assertPosition(tokens.position(), 5, 21);

            assertToken(tokens.next(), "semicolon", null);
            assertPosition(tokens.position(), 5, 29);

            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @MethodSource("provideReservedCharacterStandardTokenTypes")
    void shouldIgnoreFirstReservedCharactersAllowedByIdentifierCharacterValidatorWhenUsingStandardGrammarTokenizer(
        char c, String tokenType
    ) throws IOException {
        var source = String.valueOf(c);

        try (var tokens = initializeTokenIterator(false, source, new TestStandardIdentifierCharacterValidator())) {
            assertToken(tokens.next(), tokenType, null);
        }
    }

    @Test
    void shouldIgnoreFirstQuoteAllowedByIdentifierCharacterValidatorWhenUsingStandardGrammarTokenizer() throws IOException {
        var source = "\"abc\"";

        try (var tokens = initializeTokenIterator(false, source, new TestStandardIdentifierCharacterValidator())) {
            assertToken(tokens.next(), "string", "abc");
        }
    }

    @Test
    void shouldIgnoreFirstSpaceAllowedByIdentifierCharacterValidatorWhenUsingStandardGrammarTokenizer() throws IOException {
        var source = " ";

        try (var tokens = initializeTokenIterator(false, source, new TestStandardIdentifierCharacterValidator())) {
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldIgnoreFirstQuoteAllowedByIdentifierCharacterValidatorWhenUsingExtendedGrammarTokenizer() throws IOException {
        var source = "\"abc\"";

        try (var tokens = initializeTokenIterator(true, source, new TestExtendedIdentifierCharacterValidator())) {
            assertToken(tokens.next(), "string", "abc");
        }
    }

    @Test
    void shouldIgnoreFirstSpaceAllowedByIdentifierCharacterValidatorWhenUsingExtendedGrammarTokenizer() throws IOException {
        var source = " ";

        try (var tokens = initializeTokenIterator(true, source, new TestExtendedIdentifierCharacterValidator())) {
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @ParameterizedTest
    @MethodSource("provideReservedCharacterExtendedTokenTypes")
    void shouldIgnoreFirstReservedCharactersAllowedByIdentifierCharacterValidatorWhenUsingExtendedGrammarTokenizer(
        char c, String tokenType
    ) throws IOException {
        var source = String.valueOf(c);

        try (var tokens = initializeTokenIterator(true, source, new TestExtendedIdentifierCharacterValidator())) {
            assertToken(tokens.next(), tokenType, null);
        }
    }

    @ParameterizedTest
    @MethodSource("provideReservedCharacterStandardTokenTypes")
    void shouldIgnoreNextReservedCharactersAllowedByIdentifierCharacterValidatorWhenUsingStandardGrammarTokenizer(
        char c, String tokenType
    ) throws IOException {
        var source = "a" + c;

        try (var tokens = initializeTokenIterator(false, source, new TestStandardIdentifierCharacterValidator())) {
            assertToken(tokens.next(), "identifier", "a");
            assertToken(tokens.next(), tokenType, null);
        }
    }

    @ParameterizedTest
    @MethodSource("provideReservedCharacterExtendedTokenTypes")
    void shouldIgnoreNextReservedCharactersAllowedByIdentifierCharacterValidatorWhenUsingExtendedGrammarTokenizer(
        char c, String tokenType
    ) throws IOException {
        var source = "a" + c;

        try (var tokens = initializeTokenIterator(true, source, new TestExtendedIdentifierCharacterValidator())) {
            assertToken(tokens.next(), "identifier", "a");
            assertToken(tokens.next(), tokenType, null);
        }
    }

    @Test
    void shouldIgnoreNextQuoteAllowedByIdentifierCharacterValidatorWhenUsingStandardGrammarTokenizer() throws IOException {
        var source = "a\"abc\"";

        try (var tokens = initializeTokenIterator(false, source, new TestStandardIdentifierCharacterValidator())) {
            assertToken(tokens.next(), "identifier", "a");
            assertToken(tokens.next(), "string", "abc");
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldIgnoreNextSpaceAllowedByIdentifierCharacterValidatorWhenUsingStandardGrammarTokenizer() throws IOException {
        var source = "a ";

        try (var tokens = initializeTokenIterator(false, source, new TestStandardIdentifierCharacterValidator())) {
            assertToken(tokens.next(), "identifier", "a");
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldIgnoreNextQuoteAllowedByIdentifierCharacterValidatorWhenUsingExtendedGrammarTokenizer() throws IOException {
        var source = "a\"abc\"";

        try (var tokens = initializeTokenIterator(true, source, new TestExtendedIdentifierCharacterValidator())) {
            assertToken(tokens.next(), "identifier", "a");
            assertToken(tokens.next(), "string", "abc");
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    @Test
    void shouldIgnoreNextSpaceAllowedByIdentifierCharacterValidatorWhenUsingExtendedGrammarTokenizer() throws IOException {
        var source = "a ";

        try (var tokens = initializeTokenIterator(true, source, new TestExtendedIdentifierCharacterValidator())) {
            assertToken(tokens.next(), "identifier", "a");
            assertThat(tokens.hasNext()).isFalse();
        }
    }

    private TokenIterator<TextPosition> initializeTokenIterator(boolean extended, String source) {
        return initializeTokenIterator(extended, source, new StandardIdentifierCharacterValidator());
    }

    private TokenIterator<TextPosition> initializeTokenIterator(
        boolean extended, String source, IdentifierCharacterValidator identifierCharacterValidator
    ) {
        return new GrammarTokenizer(extended, identifierCharacterValidator, () -> new char[10])
            .getTokenIterator(new StringReader(source));
    }

    private static Stream<Arguments> provideReservedCharacterStandardTokenTypes() {
        return Stream.of(
            Arguments.of('{', "left-curly-bracket"),
            Arguments.of('}', "right-curly-bracket"),
            Arguments.of('>', "gt"),
            Arguments.of(',', "comma"),
            Arguments.of(';', "semicolon"),
            Arguments.of(':', "colon"),
            Arguments.of('!', "exclamation"),
            Arguments.of('^', "caret")
        );
    }

    private static Stream<Arguments> provideReservedCharacterExtendedTokenTypes() {
        return Stream.concat(
            provideReservedCharacterStandardTokenTypes(),
            Stream.of(
                Arguments.of('(', "left-bracket"),
                Arguments.of(')', "right-bracket"),
                Arguments.of('?', "question"),
                Arguments.of('+', "plus"),
                Arguments.of('*', "asterisk"),
                Arguments.of('|', "pipe")
            )
        );
    }

    private static Stream<Arguments> provideHexadecimalDigits() {
        return Stream
            .concat(
                Stream.concat(
                    // A-F
                    IntStream.range(65, 71).mapToObj(c -> (char) c),
                    // a-f
                    IntStream.range(97, 103).mapToObj(c -> (char) c)
                ),
                // 0-9
                IntStream.range(48, 58).mapToObj(c -> (char) c)
            )
            .map(Arguments::of);
    }

    private static Stream<Arguments> provideIdentifierAllowedFirstCharacters() {
        return Stream
            .concat(
                // Capital ASCII letters
                IntStream.range(65, 91).mapToObj(c -> (char) c),
                // Small ASCII letters
                IntStream.range(97, 123).mapToObj(c -> (char) c)
            )
            .map(Arguments::of);
    }

    private static Stream<Arguments> provideIdentifierAllowedNextCharacters() {
        return Stream
            .concat(
                provideIdentifierAllowedFirstCharacters(),
                Stream
                    .concat(
                        // Digits
                        IntStream.range(48, 58).mapToObj(c -> (char) c),
                        Stream.of('-', '_')
                    )
                    .map(Arguments::of)
            );
    }

    private static Stream<Arguments> provideEscapedAndExpectedCharacters() {
        return Stream.of(
            Arguments.of("\\t", '\t'),
            Arguments.of("\\b", '\b'),
            Arguments.of("\\n", '\n'),
            Arguments.of("\\r", '\r'),
            Arguments.of("\\f", '\f'),
            Arguments.of("\\'", '\''),
            Arguments.of("\\\"", '"'),
            Arguments.of("\\\\", '\\')
        );
    }

    private void assertToken(Token token, String type, String value) {
        assertThat(token.getType()).isEqualTo(type);
        assertThat(token.getValue()).isEqualTo(value);
    }

    private void assertPosition(TextPosition position, int line, int column) {
        assertThat(position.getLine()).isEqualTo(line);
        assertThat(position.getColumn()).isEqualTo(column);
    }

    private static class TestStandardIdentifierCharacterValidator extends StandardIdentifierCharacterValidator {
        @Override
        public boolean isValidFirstCharacter(char c) {
            return super.isValidFirstCharacter(c)
                || Character.isWhitespace(c)
                || c == '{' || c == '}' || c == '"' || c == '>' || c == ',' || c == ';' || c == ':' || c == '!';
        }

        @Override
        public boolean isValidNextCharacter(char c) {
            return super.isValidNextCharacter(c)
                || Character.isWhitespace(c)
                || c == '{' || c == '}' || c == '"' || c == '>' || c == ',' || c == ';' || c == ':' || c == '!';
        }
    }

    private static class TestExtendedIdentifierCharacterValidator extends ExtendedIdentifierCharacterValidator {
        @Override
        public boolean isValidFirstCharacter(char c) {
            return super.isValidFirstCharacter(c)
                || Character.isWhitespace(c)
                || c == '{' || c == '}' || c == '"' || c == '>' || c == ',' || c == ';' || c == ':' || c == '!'
                || c == '(' || c == ')' || c == '?' || c == '+' || c == '*' || c == '|';
        }

        @Override
        public boolean isValidNextCharacter(char c) {
            return super.isValidNextCharacter(c)
                || Character.isWhitespace(c)
                || c == '{' || c == '}' || c == '"' || c == '>' || c == ',' || c == ';' || c == ':' || c == '!'
                || c == '(' || c == ')' || c == '?' || c == '+' || c == '*' || c == '|';
        }
    }
}