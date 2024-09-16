package org.vinniks.parsla.tokenizer.text.buffered;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BufferedCharacterIteratorTest {
    @Test
    @SuppressWarnings({"DataFlowIssue", "resource"})
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileCreatingBufferedCharacterIteratorWithNullSource() {
        assertThatThrownBy(() -> new BufferedCharacterIterator(null, () -> new char[10]))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("source is marked non-null but is null");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileCreatingBufferedCharacterIteratorWithNullBuffer() {
        assertThatThrownBy(() -> new BufferedCharacterIterator(new StringReader(""), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("characterBufferProvider is marked non-null but is null");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithCorrectMessageWhileCreatingBufferedCharacterIteratorWithZeroLengthBuffer() {
        assertThatThrownBy(() -> new BufferedCharacterIterator(new StringReader(""), () -> new char[0]))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("buffer size can not be 0");
    }

    @Test
    void shouldNotEmitAnyCharactersWhenIteratingOverEmptyString() throws IOException {
        var source = "";
        var characterIterator = new BufferedCharacterIterator(new StringReader(source), () -> new char[10]);

        assertThat(characterIterator.hasNext()).isFalse();
    }

    @Test
    void shouldEmitAllCharactersWhileIteratingOverStringThatIsShorterThanBuffer() throws IOException {
        var source = "hello";
        var characterIterator = new BufferedCharacterIterator(new StringReader(source), () -> new char[10]);

        assertThat(characterIterator.next()).isEqualTo('h');
        assertThat(characterIterator.next()).isEqualTo('e');
        assertThat(characterIterator.next()).isEqualTo('l');
        assertThat(characterIterator.next()).isEqualTo('l');
        assertThat(characterIterator.next()).isEqualTo('o');
        assertThat(characterIterator.hasNext()).isFalse();
    }

    @Test
    void shouldEmitAllCharactersWhileIteratingOverStringThatIsLongerThanBuffer() throws IOException {
        var source = "hello, world";
        var characterIterator = new BufferedCharacterIterator(new StringReader(source), () -> new char[3]);

        assertThat(characterIterator.next()).isEqualTo('h');
        assertThat(characterIterator.next()).isEqualTo('e');
        assertThat(characterIterator.next()).isEqualTo('l');
        assertThat(characterIterator.next()).isEqualTo('l');
        assertThat(characterIterator.next()).isEqualTo('o');
        assertThat(characterIterator.next()).isEqualTo(',');
        assertThat(characterIterator.next()).isEqualTo(' ');
        assertThat(characterIterator.next()).isEqualTo('w');
        assertThat(characterIterator.next()).isEqualTo('o');
        assertThat(characterIterator.next()).isEqualTo('r');
        assertThat(characterIterator.next()).isEqualTo('l');
        assertThat(characterIterator.next()).isEqualTo('d');
        assertThat(characterIterator.hasNext()).isFalse();
    }
}