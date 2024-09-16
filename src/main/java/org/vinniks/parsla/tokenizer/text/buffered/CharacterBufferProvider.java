package org.vinniks.parsla.tokenizer.text.buffered;

import lombok.NonNull;

@FunctionalInterface
public interface CharacterBufferProvider {
    char[] acquireBuffer();

    default void releaseBuffer(@NonNull char[] buffer) {}
}
