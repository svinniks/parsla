package org.vinniks.parsla.grammar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public final class TokenItem implements Item {
    private final int elevation;
    private final String tokenType;
    private final boolean outputType;
    private final String tokenValue;
    private final boolean outputValue;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof TokenItem otherItem) {
            return elevation == otherItem.elevation
                && Objects.equals(tokenType, otherItem.tokenType)
                && outputType == otherItem.outputType
                && Objects.equals(tokenValue, otherItem.tokenValue)
                && outputValue == otherItem.outputValue;
        } else {
            return false;
        }
    }
}
