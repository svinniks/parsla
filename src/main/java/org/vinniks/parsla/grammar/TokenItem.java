package org.vinniks.parsla.grammar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class TokenItem implements Item {
    private final int elevation;
    private final String tokenType;
    private final boolean outputType;
    private final String tokenValue;
    private final boolean outputValue;
}
