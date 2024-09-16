package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import org.vinniks.parsla.tokenizer.Token;

@Getter(AccessLevel.PACKAGE)
final class TokenParseTreeNode<P> extends AbstractParseTreeNode<AbstractCompiledTokenItem, P> {
    private final Token token;
    private final int match;

    TokenParseTreeNode(
        AbstractParseTreeNode<?, P> parent,
        int level,
        AbstractCompiledTokenItem item,
        P position,
        Token token,
        int match
    ) {
        super(parent, level, item, position);
        this.token = token;
        this.match = match;
    }
}
