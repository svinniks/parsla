package org.vinniks.parsla.grammar.serialization;

import org.junit.jupiter.api.Test;
import org.vinniks.parsla.grammar.Grammar;

import java.io.IOException;
import java.io.StringWriter;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("ALL")
class StandardGrammarWriterTest {
    @Test
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileWritingNullGrammar() {
        var writer = new StringWriter();

        assertThatThrownBy(() -> StandardGrammarWriter.instance().write(null, writer))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("grammar is marked non-null but is null");
    }

    @Test
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileWritingNullWriter() {
        var grammar = new Grammar(emptyList());

        assertThatThrownBy(() -> StandardGrammarWriter.instance().write(grammar, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("writer is marked non-null but is null");
    }

    @Test
    void shouldWriteEmptyGrammar() throws IOException {
        var grammar = new Grammar(emptyList());

        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo("");
    }

    @Test
    void shouldGrammarWithOneEmptyOptionWithoutOutput() throws IOException {
        var source = """
            option-1: ^;
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneEmptyOptionWithOutput() throws IOException {
        var source = """
            >option-1: ^;
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneOptionItemBothWithoutOutput() throws IOException {
        var source = """
            option-1: option-2;
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneOptionItemBothWithOutput() throws IOException {
        var source = """
            >option-1: >option-2;
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithoutAnyProperties() throws IOException {
        var source = """
            >option-1: {};
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithOutputType() throws IOException {
        var source = """
            >option-1: {>};
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithTokenType() throws IOException {
        var source = """
            >option-1: {token-type-1};
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithTokenTypeAndOutputType() throws IOException {
        var source = """
            >option-1: {>token-type-1};
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithOutputValue() throws IOException {
        var source = """
            >option-1: {, >};
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithTokenValue() throws IOException {
        var source = """
            >option-1: {, "token-value-1"};
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithTokenValueAndOutpuValue() throws IOException {
        var source = """
            >option-1: {, >"token-value-1"};
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithAllTokenTypeAndValueProperties() throws IOException {
        var source = """
            >option-1: {>token-type-1, >"token-value-1"};
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithElevations() throws IOException {
        var source = """
            >option-1: {}!!!;
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldGrammarWithOneOptionWithOneTokenItemWithPropertiesAndElevations() throws IOException {
        var source = """
            >option-1: {token-type1, >"token-value-1"}!!!;
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldInsertEmptyLineBetweenOptionsWithDifferentRuleNames() throws IOException {
        var source = """
            >option-1: ^;
            
            option-2: ^;
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    @Test
    void shouldNotInsertEmptyLineBetweenOptionsWithSameRuleNames() throws IOException {
        var source = """
            >option-1: ^;
            option-1: ^;
            """;

        var grammar = Grammar.readStandard(source);
        String grammarText = writeGrammarToString(grammar);

        assertThat(grammarText).isEqualTo(source);
    }

    private String writeGrammarToString(Grammar grammar) throws IOException {
        try (var writer = new StringWriter()) {
            StandardGrammarWriter.instance().write(grammar, writer);;
            return writer.toString();
        }
    }
}