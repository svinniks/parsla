package org.vinniks.parsla.tokenizer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenTest {
    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileCreatingTokenWithNullType() {
        assertThatThrownBy(() -> new Token(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("type is marked non-null but is null");
    }

    @Test
    void shouldCreateToken() {
        var token = new Token("type", "value");

        assertThat(token.getType()).isEqualTo("type");
        assertThat(token.getValue()).isEqualTo("value");
    }

    @Test
    void shouldConvertTokenWithoutValueToString() {
        var token = new Token("type", null);

        assertThat(token.toString()).isEqualTo("type");
    }

    @Test
    void shouldConvertTokenWithValueToString() {
        var token = new Token("type", "value");

        assertThat(token.toString()).isEqualTo("type \"value\"");
    }
}