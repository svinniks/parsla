package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
final class CompiledRuleItem implements CompiledItem {
    private final String ruleName;
    private final boolean output;
    private final Collection<CompiledOption> options;
}
