package org.vinniks.parsla.grammar.serialization;

import org.junit.jupiter.api.Test;
import org.vinniks.parsla.exception.GrammarException;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.grammar.Grammar;

import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.vinniks.parsla.grammar.GrammarBuilder.items;
import static org.vinniks.parsla.grammar.GrammarBuilder.option;
import static org.vinniks.parsla.grammar.GrammarBuilder.options;
import static org.vinniks.parsla.grammar.GrammarBuilder.rule;
import static org.vinniks.parsla.grammar.GrammarBuilder.token;

class StandardGrammarReaderTest {
    @Test
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileReadingGrammarWithNullReaderSource() {
        assertThatThrownBy(() -> StandardGrammarReader.instance().read((Reader) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("source is marked non-null but is null");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileReadingGrammarWithNullStringSource() {
        assertThatThrownBy(() -> StandardGrammarReader.instance().read((String) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("source is marked non-null but is null");
    }

    @Test
    void shouldReadEmptyGrammar() {
        var source = "";
        var expectedGrammar = new Grammar(options());

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneEmptyOptionWithoutOutput() {
        var source = """
            option: ^;
            """;

        var expectedGrammar = new Grammar(options(
            option("option")
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneEmptyOptionWithOutput() {
        var source = """
            >option: ^;
            """;

        var expectedGrammar = new Grammar(options(
            option("option", true)
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithTwoEmptyOptions() {
        var source = """
            >option-1: ^;
            option-2: ^;
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", true),
            option("option-2")
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneRuleItemWithoutOutput() {
        var source = """
            option-1: option-2;
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(rule("option-2")))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneRuleItemWithOutput() {
        var source = """
            option-1: >option-2;
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(rule("option-2", true)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithMultipleRuleItems() {
        var source = """
            option-1: >option-2 option-3 >option-4;
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(rule("option-2", true), rule("option-3"), rule("option-4", true)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithoutFields() {
        var source = """
            option-1: {};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token()))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithoutFieldsAndComma() {
        var source = """
            option-1: {,};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token()))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithJustElevation() {
        var source = """
            option-1: {}!!!;
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token(3)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithJustOutputType() {
        var source = """
            option-1: {>};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token(null, true, false)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithJustOutputTypeAndComma() {
        var source = """
            option-1: {>,};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token(null, true, false)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithJustOutputValue() {
        var source = """
            option-1: {, >};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token(null, false, true)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithOutputTypeAndValue() {
        var source = """
            option-1: {>, >};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token(null, true, true)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithJustTokenType() {
        var source = """
            option-1: {token-type-1};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token("token-type-1")))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithJustTokenTypeAndComma() {
        var source = """
            option-1: {token-type-1,};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token("token-type-1")))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemWithJustTokenValue() {
        var source = """
            option-1: {, "token-value-1"};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token(null, false, "token-value-1", false)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemTokenTypeAndValue() {
        var source = """
            option-1: {token-type-1, "token-value-1"};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token("token-type-1", false, "token-value-1", false)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneRuleWithOneTokenItemTokenTypeValueAndBothOutputs() {
        var source = """
            option-1: {>token-type-1, >"token-value-1"};
            """;

        var expectedGrammar = new Grammar(options(
            option("option-1", items(token("token-type-1", true, "token-value-1", true)))
        ));

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadStandardGrammarGrammar() {
        var standardGrammarGrammar = StandardGrammarReader.grammarGrammar();
        var source = standardGrammarGrammar.toString();

        var readGrammar = StandardGrammarReader.instance().read(source);

        assertThat(readGrammar).isEqualTo(standardGrammarGrammar);
    }

    @Test
    void shouldThrowGrammarExceptionWithCorrectCauseWhenParsingExceptionInTokenizer() {
        var source = "abc %";

        assertThatThrownBy(() -> Grammar.readStandard(source))
            .isInstanceOf(GrammarException.class)
            .hasMessage("failed to read standard grammar")
            .hasRootCauseInstanceOf(ParsingException.class)
            .hasRootCauseMessage("unexpected character % at 1:5");
    }

    @Test
    void shouldThrowGrammarExceptionWithCorrectCauseWhenParsingExceptionInParser() {
        var source = "abc abc";

        assertThatThrownBy(() -> Grammar.readStandard(source))
            .isInstanceOf(GrammarException.class)
            .hasMessage("failed to read standard grammar")
            .hasRootCauseInstanceOf(ParsingException.class)
            .hasRootCauseMessage("unexpected identifier \"abc\" at 1:5");
    }
}



