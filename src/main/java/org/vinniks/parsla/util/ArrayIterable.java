package org.vinniks.parsla.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class ArrayIterable<T> implements Iterable<T> {
    @NonNull
    private final T[] array;

    private final boolean reverse;

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator<>(array, reverse);
    }

    private static class ArrayIterator<T> implements Iterator<T> {
        private final T[] array;
        private final int increment;
        private int i;

        public ArrayIterator(T[] array, boolean reverse) {
            this.array = array;

            if (reverse) {
                i = array.length - 1;
                increment = -1;
            } else {
                i = 0;
                increment = 1;
            }
        }

        @Override
        public boolean hasNext() {
            return i >= 0 && i < array.length;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            var element = array[i];
            i += increment;
            return element;
        }
    }
}
