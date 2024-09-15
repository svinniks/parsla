package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
final class RuleParseTreeNode extends AbstractParseTreeNode<CompiledRuleItem> {
    private final boolean outputOption;

    RuleParseTreeNode(AbstractParseTreeNode<?> parent, int level, CompiledRuleItem item, boolean outputOption) {
        super(parent, level, item);
        this.outputOption = outputOption;
    }
}
