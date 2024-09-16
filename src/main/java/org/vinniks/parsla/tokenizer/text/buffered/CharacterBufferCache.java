package org.vinniks.parsla.tokenizer.text.buffered;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class CharacterBufferCache implements CharacterBufferProvider {
    @Getter
    private final int bufferSize;

    @Getter
    private final int bufferCount;

    private final Set<char[]> ownedBuffers;
    private final Set<char[]> cachedBuffers;

    public CharacterBufferCache(int bufferSize, int bufferCount) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("buffer size can not be less than 1");
        }

        if (bufferCount < 0) {
            throw new IllegalArgumentException("buffer count can not be less than 0");
        }

        this.bufferSize = bufferSize;
        this.bufferCount = bufferCount;
        ownedBuffers = new HashSet<>();
        cachedBuffers = new LinkedHashSet<>();
    }

    @Override
    public synchronized char[] acquireBuffer() {
        if (!cachedBuffers.isEmpty()) {
            var buffer = cachedBuffers.iterator().next();
            cachedBuffers.remove(buffer);
            return buffer;
        } else {
            var buffer = new char[bufferSize];

            if (ownedBuffers.size() < bufferCount) {
                ownedBuffers.add(buffer);
            }

            return buffer;
        }
    }

    @Override
    public synchronized void releaseBuffer(@NonNull char[] buffer) {
        if (ownedBuffers.contains(buffer)) {
            cachedBuffers.add(buffer);
        }
    }
}

