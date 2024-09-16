package org.vinniks.parsla.grammar;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.vinniks.parsla.grammar.GrammarBuilder.token;

class TokenItemTest {
    @Test
    void shouldSuccessfullyCreateTokenItem() {
        var item = new TokenItem(5, "type", true, "value", false);

        assertThat(item.getElevation()).isEqualTo(5);
        assertThat(item.getTokenType()).isEqualTo("type");
        assertThat(item.isOutputType()).isTrue();
        assertThat(item.getTokenValue()).isEqualTo("value");
        assertThat(item.isOutputValue()).isFalse();
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void shouldReturnFalseWhileCheckingTokenItemEqualityWithItself() {
        var item = token("type-1");

        assertThat(item.equals(item)).isTrue();
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    void shouldReturnFalseWhileCheckingTokenItemEqualityWhenSecondObjectIsNotTokenItem() {
        var item = token("type-1");

        assertThat(item.equals("hello")).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingTokenItemEqualityWhenElevationsDiffer() {
        var item1 = token(1, "type-1");
        var item2 = token(2, "type-1");

        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingTokenItemEqualityWhenTokenTypesDiffer() {
        var item1 = token(1, "type-1");
        var item2 = token(1, "type-2");

        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingTokenItemEqualityWhenOutputTypesDiffer() {
        var item1 = token(1, "type-1", true, false);
        var item2 = token(1, "type-1", false, false);

        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingTokenItemEqualityWhenTokenValuesDiffer() {
        var item1 = token(1, "type-1", true, "value-1", false);
        var item2 = token(1, "type-1", true, "value-2", false);

        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    void shouldReturnFalseWhileCheckingTokenItemEqualityWhenOutputValuesDiffer() {
        var item1 = token(1, "type-1", true, "value-1", false);
        var item2 = token(1, "type-1", true, "value-1", true);

        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    void shouldReturnTrueWhileCheckingEqualTokenItemEquality() {
        var item1 = token(1, "type-1", true, "value-1", true);
        var item2 = token(1, "type-1", true, "value-1", true);

        assertThat(item1.equals(item2)).isTrue();
    }
}