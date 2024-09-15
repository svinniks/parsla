package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import org.vinniks.parsla.tokenizer.Token;

@Getter(AccessLevel.PACKAGE)
final class TokenParseTreeNode extends AbstractParseTreeNode<AbstractCompiledTokenItem> {
    private final Token token;
    private final int match;

    TokenParseTreeNode(AbstractParseTreeNode<?> parent, int level, AbstractCompiledTokenItem item, Token token, int match) {
        super(parent, level, item);
        this.token = token;
        this.match = match;
    }
}
