package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.vinniks.parsla.util.ArrayIterable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class CompiledOption {
    @Getter(AccessLevel.PACKAGE)
    private final boolean output;

    private final CompiledItem[] items;

    Iterable<CompiledItem> getItems(boolean reverse) {
        return new ArrayIterable<>(items, reverse);
    }
}
