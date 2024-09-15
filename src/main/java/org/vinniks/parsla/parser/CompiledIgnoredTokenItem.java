package org.vinniks.parsla.parser;

import org.vinniks.parsla.tokenizer.Token;

import java.util.Set;

final class CompiledIgnoredTokenItem extends AbstractCompiledTokenItem {
    public CompiledIgnoredTokenItem() {
        super(0);
    }

    @Override
    protected boolean matches(Token token, Set<String> ignoredTokenTypes) {
        return ignoredTokenTypes.contains(token.getType());
    }
}
