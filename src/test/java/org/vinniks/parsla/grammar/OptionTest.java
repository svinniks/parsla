package org.vinniks.parsla.grammar;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.vinniks.parsla.grammar.GrammarBuilder.items;
import static org.vinniks.parsla.grammar.GrammarBuilder.option;
import static org.vinniks.parsla.grammar.GrammarBuilder.rule;

class OptionTest {
    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldThrowNullPointerExceptionWhenCreatingOptionWithNullRuleName() {
        assertThatThrownBy(() -> new Option(null, true, emptyList()))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldThrowNullPointerExceptionWhenCreatingOptionWithNullItems() {
        assertThatThrownBy(() -> new Option("rule", true, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowNullPointerExceptionWithCorrectMessageWhenCreatingOptionWithNullItem() {
        var items = new ArrayList<Item>();
        items.add(null);

        assertThatThrownBy(() -> new Option("rule", true, items))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("option item can not be null");
    }

    @Test
    void shouldSuccessfullyCreateOption() {
        var items = new ArrayList<Item>();

        var option = new Option("rule", true, items);

        assertThat(option.getRuleName()).isEqualTo("rule");
        assertThat(option.isOutput()).isTrue();
        assertThat(option.getItems()).isSameAs(items);
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void shouldReturnTrueWhileCheckingOptionEqualityWithItself() {
        var option = option("rule-1");

        assertThat(option.equals(option)).isTrue();
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    void shouldReturnFalseWhileCheckingOptionEqualityWhileSecondObjectIsNotOption() {
        var option = option("rule-1");

        assertThat(option.equals("hello")).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingOptionEqualityWhenRuleNamesDiffer() {
        var option1 = option("rule-1");
        var option2 = option("rule-2");

        assertThat(option1.equals(option2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingOptionEqualityWhenOutputDiffer() {
        var option1 = option("rule-1", true);
        var option2 = option("rule-1", false);

        assertThat(option1.equals(option2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingOptionEqualityWhenFirstItemDiffer() {
        var option1 = option("rule-1", items(
            rule("rule-1")
        ));

        var option2 = option("rule-1", items(
            rule("rule-2")
        ));

        assertThat(option1.equals(option2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingOptionEqualityWhenSecondItemDiffer() {
        var option1 = option("rule-1", items(
            rule("rule-1"),
            rule("rule-2")
        ));

        var option2 = option("rule-1", items(
            rule("rule-1"),
            rule("rule-3")
        ));

        assertThat(option1.equals(option2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingOptionEqualityWhenFirstOptionHasMoreItems() {
        var option1 = option("rule-1", items(
            rule("rule-1"),
            rule("rule-2")
        ));

        var option2 = option("rule-1", items(
            rule("rule-1")
        ));

        assertThat(option1.equals(option2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingOptionEqualityWhenSecondOptionHasMoreItems() {
        var option1 = option("rule-1", items(
            rule("rule-1")
        ));

        var option2 = option("rule-1", items(
            rule("rule-1"),
            rule("rule-2")
        ));

        assertThat(option1.equals(option2)).isFalse();
    }

    @Test
    void shouldReturnTrueWhileCheckingOptionEqualityWhenAllOptionsAreEqual() {
        var option1 = option("rule-1", items(
            rule("rule-1"),
            rule("rule-2")
        ));

        var option2 = option("rule-1", items(
            rule("rule-1"),
            rule("rule-2")
        ));

        assertThat(option1.equals(option2)).isTrue();
    }
}