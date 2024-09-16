package org.vinniks.parsla.tokenizer.text.buffered;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CharacterBufferCacheTest {
    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreatingCharacterBufferCacheWithBufferSizeLessThan1() {
        assertThatThrownBy(() -> new CharacterBufferCache(0, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("buffer size can not be less than 1");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreatingCharacterBufferCacheWithBufferCountLessThan0() {
        assertThatThrownBy(() -> new CharacterBufferCache(100, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("buffer count can not be less than 0");
    }

    @Test
    void shouldCreateCharacterBufferCache() {
        var bufferCache = new CharacterBufferCache(100, 200);

        assertThat(bufferCache.getBufferSize()).isEqualTo(100);
        assertThat(bufferCache.getBufferCount()).isEqualTo(200);
    }

    @Test
    void shouldAcquireBuffer() {
        var bufferCache = new CharacterBufferCache(5, 10);

        var buffer = bufferCache.acquireBuffer();

        assertThat(buffer.length).isEqualTo(5);
    }

    @Test
    void shouldAcquireReleaseAndReacquireBuffer() {
        var bufferCache = new CharacterBufferCache(5, 10);

        var buffer1 = bufferCache.acquireBuffer();
        bufferCache.releaseBuffer(buffer1);
        var buffer2 = bufferCache.acquireBuffer();

        assertThat(buffer2).isSameAs(buffer1);
    }

    @Test
    void shouldNotCacheBufferIsCacheIsFull() {
        var bufferCache = new CharacterBufferCache(5, 2);

        bufferCache.acquireBuffer();
        bufferCache.acquireBuffer();
        var buffer3 = bufferCache.acquireBuffer();
        bufferCache.releaseBuffer(buffer3);
        var buffer4 = bufferCache.acquireBuffer();

        assertThat(buffer4).isNotSameAs(buffer3);
    }

    @Test
    void shouldNotCacheBufferWhichIsNotOwnedByTheCache() {
        var bufferCache = new CharacterBufferCache(5, 2);
        var buffer1= new char[5];
        bufferCache.releaseBuffer(buffer1);
        var buffer2 = bufferCache.acquireBuffer();

        assertThat(buffer2).isNotSameAs(buffer1);
    }

    @Test
    void shouldNotCacheSameBufferTwice() {
        var bufferCache = new CharacterBufferCache(5, 2);
        var buffer1 = bufferCache.acquireBuffer();
        bufferCache.releaseBuffer(buffer1);
        bufferCache.releaseBuffer(buffer1);
        var buffer2 = bufferCache.acquireBuffer();
        var buffer3 = bufferCache.acquireBuffer();

        assertThat(buffer2).isSameAs(buffer1);
        assertThat(buffer3).isNotSameAs(buffer1);
    }

    @Test
    void shouldReacquireMultipleBuffers() {
        var bufferCache = new CharacterBufferCache(5, 10);
        var buffer1 = bufferCache.acquireBuffer();
        var buffer2 = bufferCache.acquireBuffer();
        var buffer3 = bufferCache.acquireBuffer();
        bufferCache.releaseBuffer(buffer1);
        bufferCache.releaseBuffer(buffer2);
        bufferCache.releaseBuffer(buffer3);

        assertThat(bufferCache.acquireBuffer()).isSameAs(buffer1);
        assertThat(bufferCache.acquireBuffer()).isSameAs(buffer2);
        assertThat(bufferCache.acquireBuffer()).isSameAs(buffer3);
    }

    @Test
    void shouldThrowNullPointerExceptionWithCorrectMessageWhileReleasingNullBuffer() {
        var bufferCache = new CharacterBufferCache(5, 2);

        assertThatThrownBy(() -> bufferCache.releaseBuffer(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("buffer is marked non-null but is null");
    }
}
