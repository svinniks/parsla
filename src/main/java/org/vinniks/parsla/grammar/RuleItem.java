package org.vinniks.parsla.grammar;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public final class RuleItem implements Item {
    @NonNull
    private final String ruleName;

    private final boolean output;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof RuleItem otherItem) {
            return Objects.equals(ruleName, otherItem.ruleName) && output == otherItem.output;
        } else {
            return false;
        }
    }
}
