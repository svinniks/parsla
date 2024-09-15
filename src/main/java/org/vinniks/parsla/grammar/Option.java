package org.vinniks.parsla.grammar;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@RequiredArgsConstructor
@Getter
public class Option {
    @Getter
    @NonNull
    private final String ruleName;

    @Getter
    private final boolean output;

    @NonNull
    @Singular
    private final Iterable<@NonNull Item> items;
}
