package org.vinniks.parsla.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Validations {
    public static <T> void requireNonNullElements(Iterable<T> iterable, String message) {
        iterable.forEach(element -> {
            if (element == null) {
                throw new NullPointerException(message);
            }
        });
    }
}
