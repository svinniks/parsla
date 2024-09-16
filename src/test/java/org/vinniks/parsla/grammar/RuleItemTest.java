package org.vinniks.parsla.grammar;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.vinniks.parsla.grammar.GrammarBuilder.rule;

@SuppressWarnings("ALL")
class RuleItemTest {
    @Test
    void shouldThrowNullPointerExceptionWhenCreatingRuleItemWithNullRuleName() {
        assertThatThrownBy(() -> new RuleItem(null, true)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldSuccessfullyCreateRuleItem() {
        var item = new RuleItem("rule", true);

        assertThat(item.getRuleName()).isEqualTo("rule");
        assertThat(item.isOutput()).isTrue();
    }

    @Test
    void shouldReturnTrueWhileCheckingRuleItemEqualityWithItself() {
        var item = rule("rule-1");

        assertThat(item.equals(item)).isTrue();
    }

    @Test
    void shouldReturnFalseWhileCheckingRuleItemEqualityWhenSecondObjectIsNotRuleItem() {
        var item = rule("rule-1");

        assertThat(item.equals("hello")).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingRuleItemEqualityWhenRuleNamesDiffer() {
        var item1 = rule("rule-1");
        var item2 = rule("rule-2");

        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingRuleItemEqualityWhenOutputsDiffer() {
        var item1 = rule("rule-1", true);
        var item2 = rule("rule-1", false);

        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    void shouldReturnTrueWhileCheckingEqualRuleItemEquality() {
        var item1 = rule("rule-1", true);
        var item2 = rule("rule-1", true);

        assertThat(item1.equals(item2)).isTrue();
    }
}