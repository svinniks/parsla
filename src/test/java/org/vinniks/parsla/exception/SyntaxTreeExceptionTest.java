package org.vinniks.parsla.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SyntaxTreeExceptionTest {
    @Test
    void shouldCreateSyntaxTreeExceptionWithMessage() {
        var exception = new SyntaxTreeException("hello");

        assertThat(exception).hasMessage("hello");
    }
}