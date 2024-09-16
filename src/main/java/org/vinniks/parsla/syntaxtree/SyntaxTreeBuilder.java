package org.vinniks.parsla.syntaxtree;

import org.vinniks.parsla.exception.SyntaxTreeException;
import org.vinniks.parsla.parser.ParserOutputListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import static java.util.Collections.emptyList;

public class SyntaxTreeBuilder<P> implements ParserOutputListener<P> {
    private final Deque<SyntaxTreeNode<P>> nodeStack;

    public SyntaxTreeBuilder() {
        this.nodeStack = new ArrayDeque<>();
    }

    @Override
    public void enter(P position, String value) {
        var node = new SyntaxTreeNode<>(position, value, new ArrayList<>());
        addChild(node);
        nodeStack.push(node);
    }

    @Override
    public void exit() {
        if (nodeStack.isEmpty()) {
            throw new SyntaxTreeException("failed to exit syntax tree top");
        }

        nodeStack.pop();
    }

    @Override
    public void tap(P position, String value) {
        addChild(new SyntaxTreeNode<>(position, value, emptyList()));
    }

    private void addChild(SyntaxTreeNode<P> node) {
        if (!nodeStack.isEmpty()) {
            nodeStack.getFirst().children().add(node);
        }
    }

    public SyntaxTreeNode<P> build() {
        return nodeStack.getFirst();
    }
}
