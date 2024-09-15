package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.vinniks.parsla.parser.Parser.AbstractLookAheadTreeNode;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
class Path<T extends AbstractParseTreeNode<?>> {
    private final T parseTreeNode;
    private final AbstractLookAheadTreeNode<?> lookAheadTreeNode;
}
