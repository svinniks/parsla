package org.vinniks.parsla.parser;

import java.util.ArrayDeque;
import java.util.Deque;

class ParserOutput {
    private final ParserOutputListener listener;
    private final Deque<Integer> levelStack;

    ParserOutput(ParserOutputListener listener) {
        this.listener = listener;
        levelStack = new ArrayDeque<>();
        levelStack.push(0);
    }

    void next(AbstractParseTreeNode<?> parseTreeNode) {
        if (parseTreeNode.getParent() != null) {
            next(parseTreeNode.getParent());
        }

        ensureSiblingLevel(parseTreeNode.getLevel());

        if (parseTreeNode instanceof TokenParseTreeNode tokenNode) {
            var outputValue = tokenNode.getItem().outputValue() && tokenNode.getToken().getValue() != null;

            if (tokenNode.getItem().outputType()) {
                if (outputValue) {
                    listener.enter(tokenNode.getToken().getType());
                    listener.tap(tokenNode.getToken().getValue());
                    listener.exit();
                } else {
                    listener.tap(tokenNode.getToken().getType());
                }
            } else if (outputValue) {
                listener.tap(tokenNode.getToken().getValue());
            }
        } else if (parseTreeNode instanceof RuleParseTreeNode ruleNode && (ruleNode.isOutputOption() || ruleNode.getItem().isOutput())) {
            listener.enter(ruleNode.getItem().getRuleName());
            levelStack.push(parseTreeNode.getLevel());
        }
    }

    void end() {
        ensureSiblingLevel(2);
    }

    @SuppressWarnings("ConstantConditions")
    private void ensureSiblingLevel(int level) {
        var topLevel = levelStack.peek();

        while (level <= topLevel) {
            levelStack.pop();
            listener.exit();
            topLevel = levelStack.peek();
        }
    }
}
