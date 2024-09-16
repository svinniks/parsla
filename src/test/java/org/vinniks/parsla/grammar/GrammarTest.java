package org.vinniks.parsla.grammar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vinniks.parsla.grammar.serialization.ExtendedGrammarReader;
import org.vinniks.parsla.grammar.serialization.StandardGrammarReader;
import org.vinniks.parsla.grammar.serialization.StandardGrammarWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.vinniks.parsla.grammar.GrammarBuilder.option;
import static org.vinniks.parsla.grammar.GrammarBuilder.options;

@ExtendWith(MockitoExtension.class)
class GrammarTest {
    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldThrowNullPointerExceptionWhenGrammarConstructedWithNullOptions() {
        assertThatThrownBy(() -> new Grammar(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowNullPointExceptionWhenGrammarConstructedWithNullOption() {
        var options = new ArrayList<Option>();
        options.add(null);

        assertThatThrownBy(() -> new Grammar(options))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("grammar option can not be null");
    }

    @Test
    void shouldSuccessfullyCreateGrammar() {
        var options = new ArrayList<Option>();

        var grammar = new Grammar(options);

        assertThat(grammar.getOptions()).isSameAs(options);
    }

    @Test
    void shouldCreateNewStringWriterAndCallDefaultInstanceFoStandardGrammarWriteWhenConvertingGrammarToString() throws IOException {
        var standardGrammarWriter = mock(StandardGrammarWriter.class);

        try (
            var standardGrammarWriterStatic = Mockito.mockStatic(StandardGrammarWriter.class);
            var stringWriterConstruction = Mockito.mockConstruction(
                StringWriter.class,
                (stringWriter, context) -> when(stringWriter.toString()).thenReturn("Hello, World!")
            )
        ) {
            standardGrammarWriterStatic.when(StandardGrammarWriter::instance).thenReturn(standardGrammarWriter);
            var grammar = new Grammar(emptyList());

            var result = grammar.toString();

            assertThat(result).isEqualTo("Hello, World!");
            assertThat(stringWriterConstruction.constructed()).hasSize(1);
            var constructedStringWriter = stringWriterConstruction.constructed().get(0);
            verify(standardGrammarWriter).write(grammar, constructedStringWriter);
        }
    }

    @Test
    void shouldCallStandardReaderDefaultInstanceWhenReadingGrammarFromStandardSyntaxReader() throws IOException {
        var grammar = new Grammar(emptyList());
        var source = mock(Reader.class);
        var grammarReader = mock(StandardGrammarReader.class);
        when(grammarReader.read(source)).thenReturn(grammar);

        try (var standardGrammarReaderStatic = mockStatic(StandardGrammarReader.class)) {
            standardGrammarReaderStatic.when(StandardGrammarReader::instance).thenReturn(grammarReader);

            var result = Grammar.readStandard(source);

            assertThat(result).isSameAs(grammar);
        }
    }

    @Test
    void shouldCallStandardReaderDefaultInstanceWhenReadingGrammarFromStandardSyntaxString() {
        var grammar = new Grammar(emptyList());
        var source = "Hello, World!";
        var grammarReader = mock(StandardGrammarReader.class);
        when(grammarReader.read(source)).thenReturn(grammar);

        try (var standardGrammarReaderStatic = mockStatic(StandardGrammarReader.class)) {
            standardGrammarReaderStatic.when(StandardGrammarReader::instance).thenReturn(grammarReader);

            var result = Grammar.readStandard(source);

            assertThat(result).isSameAs(grammar);
        }
    }

    @Test
    void shouldCallExtendedReaderDefaultInstanceWhenReadingGrammarFromExtendedSyntaxReader() throws IOException {
        var grammar = new Grammar(emptyList());
        var source = mock(Reader.class);
        var grammarReader = mock(ExtendedGrammarReader.class);
        when(grammarReader.read(source)).thenReturn(grammar);

        try (var extendedGrammarReaderStatic = mockStatic(ExtendedGrammarReader.class)) {
            extendedGrammarReaderStatic.when(ExtendedGrammarReader::instance).thenReturn(grammarReader);

            var result = Grammar.readExtended(source);

            assertThat(result).isSameAs(grammar);
        }
    }

    @Test
    void shouldCallExtendedReaderDefaultInstanceWhenReadingGrammarFromExtendedSyntaxString() {
        var grammar = new Grammar(emptyList());
        var source = "Hello, World!";
        var grammarReader = mock(ExtendedGrammarReader.class);
        when(grammarReader.read(source)).thenReturn(grammar);

        try(var extendedGrammarReaderStatic = mockStatic(ExtendedGrammarReader.class)) {
            extendedGrammarReaderStatic.when(ExtendedGrammarReader::instance).thenReturn(grammarReader);

            var result = Grammar.readExtended(source);

            assertThat(result).isSameAs(grammar);
        }
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void shouldReturnTrueWhileCheckingGrammarEqualityWithSelf() {
        var grammar = new Grammar(emptyList());

        assertThat(grammar.equals(grammar)).isTrue();
    }

    @Test
    @SuppressWarnings({"EqualsBetweenInconvertibleTypes"})
    void shouldReturnFalseWhileCheckingGrammarEqualityWhenOtherObjectIsNotGrammar() {
        var grammar = new Grammar(emptyList());

        assertThat(grammar.equals("hello")).isFalse();
    }

    @Test
    void shouldReturnTrueWhileCheckingGrammarEqualityForTwoEmptyGrammars() {
        var grammar1 = new Grammar(emptyList());
        var grammar2 = new Grammar(emptyList());

        assertThat(grammar1.equals(grammar2)).isTrue();
    }

    @Test
    void shouldReturnFalseWhileCheckingGrammarEqualityWhenFirstOptionsDiffers() {
        var grammar1 = new Grammar(options(
            option("rule-1")
        ));

        var grammar2 = new Grammar(options(option(
            "rule-2")
        ));

        assertThat(grammar1.equals(grammar2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingGrammarEqualityWhenSecondOptionsDiffers() {
        var grammar1 = new Grammar(options(
            option("rule-1"),
            option("rule-2")
        ));

        var grammar2 = new Grammar(options(
            option("rule-1"),
            option("rule-3")
        ));

        assertThat(grammar1.equals(grammar2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingGrammarEqualityWhenFirstGrammarHasMoreOptions() {
        var grammar1 = new Grammar(options(
            option("rule-1"),
            option("rule-2")
        ));

        var grammar2 = new Grammar(options(
            option("rule-1")
        ));

        assertThat(grammar1.equals(grammar2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingGrammarEqualityWhenSecondGrammarHasMoreOptions() {
        var grammar1 = new Grammar(options(
            option("rule-1")
        ));

        var grammar2 = new Grammar(options(
            option("rule-1"),
            option("rule-2" )
        ));

        assertThat(grammar1.equals(grammar2)).isFalse();
    }

    @Test
    void shouldReturnTrueWhileCheckingGrammarEqualityWhenAllOptionsAreEqual() {
        var grammar1 = new Grammar(options(
            option("rule-1"),
            option("rule-2")
        ));

        var grammar2 = new Grammar(options(
            option("rule-1"),
            option("rule-2")
        ));

        assertThat(grammar1.equals(grammar2)).isTrue();
    }
}