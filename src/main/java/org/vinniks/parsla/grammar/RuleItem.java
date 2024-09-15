package org.vinniks.parsla.grammar;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class RuleItem implements Item {
    @NonNull
    private final String ruleName;

    private final boolean output;
}
