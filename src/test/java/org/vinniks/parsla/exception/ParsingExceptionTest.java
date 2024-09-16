package org.vinniks.parsla.exception;

import org.junit.jupiter.api.Test;
import org.vinniks.parsla.tokenizer.text.TextPosition;

import static org.assertj.core.api.Assertions.assertThat;

class ParsingExceptionTest {
    @Test
    void shouldCreateParsingExceptionWithCorrectMessageWithNullPosition() {
        var exception = new ParsingException("hello", null);

        assertThat(exception).hasMessage("hello");
        assertThat((TextPosition) exception.getPosition()).isNull();
    }

    @Test
    void shouldCreateParsingExceptionWithCorrectMessageWithPosition() {
        var position = new TextPosition(1, 2);
        var exception = new ParsingException("hello", position);

        assertThat(exception).hasMessage("hello at 1:2");
        assertThat((TextPosition) exception.getPosition()).isEqualTo(position);
    }
}