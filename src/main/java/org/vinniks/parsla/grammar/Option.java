package org.vinniks.parsla.grammar;

import lombok.Getter;
import lombok.NonNull;

import static org.vinniks.parsla.util.Validations.requireNonNullElements;

@Getter
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Option otherOption) {
            if (!ruleName.equals(otherOption.ruleName) || output != otherOption.output) {
                return false;
            }

            var itemIterator = items.iterator();
            var otherItemIterator = otherOption.items.iterator();

            while (itemIterator.hasNext() && otherItemIterator.hasNext()) {
                if (!itemIterator.next().equals(otherItemIterator.next())) {
                    return false;
                }
            }

            return !itemIterator.hasNext() && !otherItemIterator.hasNext();
        } else {
            return false;
        }
    }
}
