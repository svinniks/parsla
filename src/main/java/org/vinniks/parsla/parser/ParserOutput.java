package org.vinniks.parsla.parser;

import java.util.ArrayDeque;
import java.util.Deque;

class ParserOutput<P> {
    private final ParserOutputListener<P> listener;
    private final Deque<Integer> levelStack;

    ParserOutput(ParserOutputListener<P> listener) {
        this.listener = listener;
        levelStack = new ArrayDeque<>();
        levelStack.push(0);
    }

    void next(AbstractParseTreeNode<?, P> parseTreeNode) {
        if (parseTreeNode.getParent() != null) {
            next(parseTreeNode.getParent());
        }

        ensureSiblingLevel(parseTreeNode.getLevel());

        if (parseTreeNode instanceof TokenParseTreeNode<?> genericTokenNode) {
            @SuppressWarnings("unchecked")
            var tokenNode = (TokenParseTreeNode<P>) genericTokenNode;

            var outputValue = tokenNode.getItem().outputValue() && tokenNode.getToken().getValue() != null;

            if (tokenNode.getItem().outputType()) {
                if (outputValue) {
                    listener.enter(tokenNode.getPosition(), tokenNode.getToken().getType());
                    listener.tap(tokenNode.getPosition(), tokenNode.getToken().getValue());
                    listener.exit();
                } else {
                    listener.tap(tokenNode.getPosition(), tokenNode.getToken().getType());
                }
            } else if (outputValue) {
                listener.tap(tokenNode.getPosition(), tokenNode.getToken().getValue());
            }
        } else if (parseTreeNode instanceof RuleParseTreeNode<?> genericRuleNode && (genericRuleNode.isOutputOption() || genericRuleNode.getItem().isOutput())) {
            @SuppressWarnings("unchecked")
            var ruleNode = (RuleParseTreeNode<P>) genericRuleNode;

            listener.enter(ruleNode.getPosition(), ruleNode.getItem().getRuleName());
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
