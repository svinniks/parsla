package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.vinniks.parsla.tokenizer.Token;

import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
sealed abstract class AbstractCompiledTokenItem implements CompiledItem permits CompiledRegularTokenItem, CompiledIgnoredTokenItem {
    private final int elevation;

    protected abstract boolean matches(Token token, Set<String> ignoredTokenTypes);

    protected boolean outputType() {
        return false;
    }

    protected boolean outputValue() {
        return false;
    }

    int match(Token token, Set<String> ignoredTokenTypes) {
        return matches(token, ignoredTokenTypes) ? elevation + 1 : 0;
    }
}
