package org.vinniks.parsla.grammar.serialization;

import org.junit.jupiter.api.Test;
import org.vinniks.parsla.exception.GrammarException;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.parser.text.TextParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExtendedGrammarReaderTest {
    @Test
    void shouldReadGrammarWithOneEmptyOptionWithoutOutput() {
        var source = """
            option-1: ^;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: ^;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneEmptyOptionWithOutput() {
        var source = """
            >option-1: ^;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            >option-1: ^;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneRuleWithoutOutput() {
        var source = """
            option-1: option-2;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: option-2;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneRuleWithOutput() {
        var source = """
            option-1: >option-2;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: >option-2;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithoutProperties() {
        var source = """
            option-1: {};""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {};""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithOutputType() {
        var source = """
            option-1: {>};""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {>};""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithTokenType() {
        var source = """
            option-1: {token-type-1};""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {token-type-1};""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithTokenTypeAndOutputType() {
        var source = """
            option-1: {>token-type-1};""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {>token-type-1};""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithOutputValue() {
        var source = """
            option-1: {, >};""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {, >};""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithTokenValue() {
        var source = """
            option-1: {, "token-value-1"};""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {, "token-value-1"};""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithTokenValueAndOutputValue() {
        var source = """
            option-1: {, >"token-value-1"};""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {, >"token-value-1"};""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithElevations() {
        var source = """
            option-1: {}!!!;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {}!!!;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenWithAllPropertiesAndElevations() {
        var source = """
            option-1: {>token-type-1, >"token-value-1"}!!!;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {>token-type-1, >"token-value-1"}!!!;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithMultipleOptionsWithMultipleItems() {
        var source = """
            option-1: >option-2 {, >}!;
            option-2: ^;
            option-2: {token-type-1, >} >option-1;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: >option-2 {, >}!;
            option-2: ^;
            option-2: {token-type-1, >} >option-1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithTwoEmptySequences() {
        var source = """
            option-1: ^ | ^;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: ^;
            option-1: ^;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithTwoSequencesOfOneRuleItem() {
        var source = """
            option-1: option-2 | option-3;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: option-2;
            option-1: option-3;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithTwoSequencesOfOneTokenItem() {
        var source = """
            option-1: {token-type-1, >} | {>, "token-value-1"};""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {token-type-1, >};
            option-1: {>, "token-value-1"};""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithThreeLongerSequences() {
        var source = """
            option-1: {token-type-1, >} >option-2 | option-3 option-4 {>, "token-value-1"} | ^;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = Grammar.readStandard("""
            option-1: {token-type-1, >} >option-2;
            option-1: option-3 option-4 {>, "token-value-1"};
            option-1: ^;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneRuleItemWithZeroOrOne() {
        var source = """
            option-1: option-2?;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1: ^;
            option-1#1: option-2;

            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneRuleItemWithOneOrMany() {
        var source = """
            option-1: option-2+;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1_: ^;
            option-1#1_: option-2 option-1#1_;
        
            option-1#1: option-2 option-1#1_;
        
            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneRuleItemWithZeroOrMany() {
        var source = """
            option-1: option-2*;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1: ^;
            option-1#1: option-2 option-1#1;
    
            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenItemWithZeroOrOne() {
        var source = """
            option-1: {>token-type-1}!?;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1: ^;
            option-1#1: {>token-type-1}!;

            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenItemWithOneOrMany() {
        var source = """
            option-1: {>token-type-1}!+;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1_: ^;
            option-1#1_: {>token-type-1}! option-1#1_;
        
            option-1#1: {>token-type-1}! option-1#1_;
        
            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneTokenItemWithZeroOrMany() {
        var source = """
            option-1: {>token-type-1}!*;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1: ^;
            option-1#1: {>token-type-1}! option-1#1;
    
            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithSubSequenceWithEmpty() {
        var source = """
            option-1: (^);""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1: ^;
            
            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithSubSequenceWithMultipleItems() {
        var source = """
            option-1: (>option-2 {>token-type-1}!);""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1: >option-2 {>token-type-1}!;
            
            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOnDeeplyNestedSubSequenceWithMultipleItems() {
        var source = """
            option-1: (((>option-2 {>token-type-1}!)));""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1#1#1: >option-2 {>token-type-1}!;
            
            option-1#1#1: option-1#1#1#1;
            
            option-1#1: option-1#1#1;
            
            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneSubsequenceWithTwoNestedSubSequencesWithMultipleItems() {
        var source = """
            option-1: ((>option-2 {>token-type-1}!) (^));""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1#1: >option-2 {>token-type-1}!;
            
            option-1#1#2: ^;

            option-1#1: option-1#1#1 option-1#1#2;

            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneSubsequenceWithTwoNestedSubSequencesWithMultipleItemsAndQuantifiers() {
        var source = """
            option-1: ((>option-2 {>token-type-1}!)+ (^)*);""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1#1: >option-2 {>token-type-1}!;
            
            option-1#1#2_: ^;
            option-1#1#2_: option-1#1#1 option-1#1#2_;
            
            option-1#1#2: option-1#1#1 option-1#1#2_;
            
            option-1#1#3: ^;
            
            option-1#1#4: ^;
            option-1#1#4: option-1#1#3 option-1#1#4;
            
            option-1#1: option-1#1#2 option-1#1#4;
            
            option-1: option-1#1;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldReadGrammarWithOneOptionWithOneSubsequenceWithQuantifierAndWithTwoNestedSubSequences() {
        var source = """
            option-1: ((>option-2 {>token-type-1}!) (^))+;""";

        var readGrammar = Grammar.readExtended(source);

        var expectedGrammar = readExtendedStandardGrammar("""
            option-1#1#1: >option-2 {>token-type-1}!;
            
            option-1#1#2: ^;
            
            option-1#1: option-1#1#1 option-1#1#2;
            
            option-1#2_: ^;
            option-1#2_: option-1#1 option-1#2_;
            
            option-1#2: option-1#1 option-1#2_;
            
            option-1: option-1#2;""");

        assertThat(readGrammar).isEqualTo(expectedGrammar);
    }

    @Test
    void shouldThrowGrammarExceptionWithCorrectCauseWhenParsingExceptionInTokenizer() {
        var source = "abc %";

        assertThatThrownBy(() -> Grammar.readExtended(source))
            .isInstanceOf(GrammarException.class)
            .hasMessage("failed to read extended grammar")
            .hasRootCauseInstanceOf(ParsingException.class)
            .hasRootCauseMessage("unexpected character % at 1:5");
    }

    @Test
    void shouldThrowGrammarExceptionWithCorrectCauseWhenParsingExceptionInParser() {
        var source = "abc abc";

        assertThatThrownBy(() -> Grammar.readExtended(source))
            .isInstanceOf(GrammarException.class)
            .hasMessage("failed to read extended grammar")
            .hasRootCauseInstanceOf(ParsingException.class)
            .hasRootCauseMessage("unexpected identifier \"abc\" at 1:5");
    }

    private Grammar readExtendedStandardGrammar(String source) {
        var identifierCharacterValidator = new ExtendedIdentifierCharacterValidator();
        var grammarTokenizer = new GrammarTokenizer(false, identifierCharacterValidator, () -> new char[8 * 1024]);
        var parser = new TextParser(StandardGrammarReader.grammarGrammar(), grammarTokenizer);
        var syntaxTree = parser.parse(source, "options");
        return StandardGrammarBuilder.build(syntaxTree);
    }
}




