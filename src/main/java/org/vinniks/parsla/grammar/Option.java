package org.vinniks.parsla.grammar;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import static org.vinniks.parsla.util.Validations.requireNonNullElements;

@Getter
@EqualsAndHashCode
public final class Option {
    private final String ruleName;
    private final boolean output;
    private final Iterable<Item> items;

    public Option(@NonNull String ruleName, boolean output, @NonNull Iterable<Item> items) {
        requireNonNullElements(items, "option item can not be null");
        this.ruleName = ruleName;
        this.output = output;
        this.items = items;
    }
}
