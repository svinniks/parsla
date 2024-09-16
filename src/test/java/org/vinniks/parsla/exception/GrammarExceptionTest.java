package org.vinniks.parsla.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GrammarExceptionTest {
    @Test
    void shouldCreateExceptionWithMessageWithoutCause() {
        var exception = new GrammarException("hello");

        assertThat(exception)
            .hasMessage("hello")
            .hasNoCause();
    }

    @Test
    void shouldCreateExceptionWithMessageWithCause() {
        var cause = new RuntimeException();
        var exception = new GrammarException("hello", cause);

        assertThat(exception)
            .hasMessage("hello")
            .hasCause(cause);
    }
}