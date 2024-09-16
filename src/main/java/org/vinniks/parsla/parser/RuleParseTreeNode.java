package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
final class RuleParseTreeNode<P> extends AbstractParseTreeNode<CompiledRuleItem, P> {
    private final boolean outputOption;

    RuleParseTreeNode(
        AbstractParseTreeNode<?, P> parent, int level, CompiledRuleItem item, P position, boolean outputOption
    ) {
        super(parent, level, item, position);
        this.outputOption = outputOption;
    }
}
