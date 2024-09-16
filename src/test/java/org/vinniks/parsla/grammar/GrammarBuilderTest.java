package org.vinniks.parsla.grammar;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.vinniks.parsla.grammar.GrammarBuilder.grammar;
import static org.vinniks.parsla.grammar.GrammarBuilder.items;
import static org.vinniks.parsla.grammar.GrammarBuilder.option;
import static org.vinniks.parsla.grammar.GrammarBuilder.options;
import static org.vinniks.parsla.grammar.GrammarBuilder.rule;
import static org.vinniks.parsla.grammar.GrammarBuilder.token;

class GrammarBuilderTest {
    @Test
    void shouldSuccessfullyCreateOptionWithAllArgumentMethod() {
        var items = new ArrayList<Item>();

        var option = option("rule", true, items);

        assertThat(option.getRuleName()).isEqualTo("rule");
        assertThat(option.isOutput()).isTrue();
        assertThat(option.getItems()).isSameAs(items);
    }

    @Test
    void shouldSuccessfullyCreateOptionWithDefaultOutputMethod() {
        var items = new ArrayList<Item>();

        var option = option("rule", items);

        assertThat(option.getRuleName()).isEqualTo("rule");
        assertThat(option.isOutput()).isFalse();
        assertThat(option.getItems()).isSameAs(items);
    }

    @Test
    void shouldSuccessfullyCreateOptionWithNoItemMethod() {
        var option = option("rule", true);

        assertThat(option.getRuleName()).isEqualTo("rule");
        assertThat(option.isOutput()).isTrue();
        assertThat(option.getItems()).isEmpty();
    }

    @Test
    void shouldSuccessfullyCreateOptionWithDefaultOutputAndNoItemMethod() {
        var option = option("rule");

        assertThat(option.getRuleName()).isEqualTo("rule");
        assertThat(option.isOutput()).isFalse();
        assertThat(option.getItems()).isEmpty();
    }

    @Test
    void shouldSuccessfullyCreateRuleItemWithAllArgumentMethod() {
        var item = rule("rule", true);

        assertThat(item.getRuleName()).isEqualTo("rule");
        assertThat(item.isOutput()).isTrue();
    }

    @Test
    void shouldSuccessfullyCreateRuleItemWithDefaultOutputMethod() {
        var item = rule("rule");

        assertThat(item.getRuleName()).isEqualTo("rule");
        assertThat(item.isOutput()).isFalse();
    }

    @Test
    void shouldSuccessfullyCreateTokenItemWithAllArgumentMethod() {
        var item = token(5, "type", true, "value", true);

        assertThat(item.getElevation()).isEqualTo(5);
        assertThat(item.getTokenType()).isEqualTo("type");
        assertThat(item.isOutputType()).isTrue();
        assertThat(item.getTokenValue()).isEqualTo("value");
        assertThat(item.isOutputValue()).isTrue();
    }

    @Test
    void shouldSuccessfullyCreateTokenItemWithNoValueArgumentMethod() {
        var item = token(5, "type", true, true);

        assertThat(item.getElevation()).isEqualTo(5);
        assertThat(item.getTokenType()).isEqualTo("type");
        assertThat(item.isOutputType()).isTrue();
        assertThat(item.getTokenValue()).isNull();
        assertThat(item.isOutputValue()).isTrue();
    }

    @Test
    void shouldSuccessfullyCreateTokenItemWithNoValueAndDefaultOutputArgumentMethod() {
        var item = token(5, "type");

        assertThat(item.getElevation()).isEqualTo(5);
        assertThat(item.getTokenType()).isEqualTo("type");
        assertThat(item.isOutputType()).isFalse();
        assertThat(item.getTokenValue()).isNull();
        assertThat(item.isOutputValue()).isFalse();
    }

    @Test
    void shouldSuccessfullyCreateTokenItemWithJustElevationArgumentMethod() {
        var item = token(5);

        assertThat(item.getElevation()).isEqualTo(5);
        assertThat(item.getTokenType()).isNull();
        assertThat(item.isOutputType()).isFalse();
        assertThat(item.getTokenValue()).isNull();
        assertThat(item.isOutputValue()).isFalse();
    }

    @Test
    void shouldSuccessfullyCreateTokenItemWithDefaultElevationMethod() {
        var item = token("type", true, "value", true);

        assertThat(item.getElevation()).isEqualTo(0);
        assertThat(item.getTokenType()).isEqualTo("type");
        assertThat(item.isOutputType()).isTrue();
        assertThat(item.getTokenValue()).isEqualTo("value");
        assertThat(item.isOutputValue()).isTrue();
    }

    @Test
    void shouldSuccessfullyCreateTokenItemWithDefaultElevationAndNoValueArgumentMethod() {
        var item = token("type", true, true);

        assertThat(item.getElevation()).isEqualTo(0);
        assertThat(item.getTokenType()).isEqualTo("type");
        assertThat(item.isOutputType()).isTrue();
        assertThat(item.getTokenValue()).isNull();
        assertThat(item.isOutputValue()).isTrue();
    }

    @Test
    void shouldSuccessfullyCreateTokenItemWithDefaultElevationAndNoValueAndDefaultOutputArgumentMethod() {
        var item = token("type");

        assertThat(item.getElevation()).isEqualTo(0);
        assertThat(item.getTokenType()).isEqualTo("type");
        assertThat(item.isOutputType()).isFalse();
        assertThat(item.getTokenValue()).isNull();
        assertThat(item.isOutputValue()).isFalse();
    }

    @Test
    void shouldSuccessfullyCreateTokenItemWithNoArgumentMethod() {
        var item = token();

        assertThat(item.getElevation()).isEqualTo(0);
        assertThat(item.getTokenType()).isNull();
        assertThat(item.isOutputType()).isFalse();
        assertThat(item.getTokenValue()).isNull();
        assertThat(item.isOutputValue()).isFalse();
    }

    @Test
    void shouldSuccessfullyCreateIterableOfItems() {
        var item1 = rule("rule");
        var item2 = token("type");

        var items = items(item1, item2);

        assertThat(items).containsExactly(item1, item2);
    }

    @Test
    void shouldSuccessfullyCreateIterableOfOptions() {
        var option1 = option("rule-1");
        var option2 = option("rule-2");

        var options = options(option1, option2);

        assertThat(options).containsExactly(option1, option2);
    }

    @Test
    void shouldSuccessfullyCreateGrammar() {
        var options = options();

        var grammar = grammar(options);

        assertThat(grammar.getOptions()).isSameAs(options);
    }
}