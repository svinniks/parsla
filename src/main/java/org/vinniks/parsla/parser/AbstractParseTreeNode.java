package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PACKAGE)
abstract sealed class AbstractParseTreeNode<T extends CompiledItem, P> permits RuleParseTreeNode, TokenParseTreeNode {
    private final AbstractParseTreeNode<?, P> parent;
    private final int level;
    private final T item;
    private final P position;
}
