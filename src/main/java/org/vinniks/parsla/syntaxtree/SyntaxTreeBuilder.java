package org.vinniks.parsla.syntaxtree;

import org.vinniks.parsla.exception.SyntaxTreeException;
import org.vinniks.parsla.parser.ParserOutputListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import static java.util.Collections.emptyList;

public class SyntaxTreeBuilder implements ParserOutputListener {
    private final Deque<SyntaxTreeNode> nodeStack;

    public SyntaxTreeBuilder() {
        this.nodeStack = new ArrayDeque<>();
    }

    @Override
    public void enter(String value) {
        var node = new SyntaxTreeNode(value, new ArrayList<>());
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
    public void tap(String value) {
        addChild(new SyntaxTreeNode(value, emptyList()));
    }

    private void addChild(SyntaxTreeNode node) {
        if (!nodeStack.isEmpty()) {
            nodeStack.getFirst().children().add(node);
        }
    }

    public SyntaxTreeNode build() {
        return nodeStack.getFirst();
    }
}
