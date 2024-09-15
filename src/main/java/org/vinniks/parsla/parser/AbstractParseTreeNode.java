package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PACKAGE)
abstract sealed class AbstractParseTreeNode<T extends CompiledItem> permits RuleParseTreeNode, TokenParseTreeNode {
    private final AbstractParseTreeNode<?> parent;
    private final int level;
    private final T item;
}
