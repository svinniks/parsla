package org.vinniks.parsla.syntaxtree;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.vinniks.parsla.exception.SyntaxTreeException;
import org.vinniks.parsla.syntaxtree.serialization.DefaultSyntaxTreeWriter;

import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SyntaxTreeNode {
    private final String value;
    List<SyntaxTreeNode> children;

    public String value() {
        return value;
    }

    public List<SyntaxTreeNode> children() {
        return children;
    }

    @Override
    @SneakyThrows
    public String toString() {
        try (var writer = new StringWriter()) {
            new DefaultSyntaxTreeWriter().write(this, writer);
            return writer.toString();
        }
    }

    public boolean valueIs(String value) {
        return this.value.equals(value);
    }

    public Optional<SyntaxTreeNode> optionalChild(String childValue) {
        var matchingChildren = children
            .stream()
            .filter(childNode -> childNode.value.equals(childValue))
            .toList();

        if (matchingChildren.size() > 1) {
            throw new SyntaxTreeException(String.format("%s has multiple %s!", value, childValue));
        } else {
            return matchingChildren.stream().findFirst();
        }
    }

    public boolean hasChild(String childValue) {
        return optionalChild(childValue).isPresent();
    }

    public SyntaxTreeNode child(String childValue) {
        return optionalChild(childValue).orElseThrow(
            () -> new SyntaxTreeException(String.format("%s does not contain %s!", value, childValue))
        );
    }

    public Optional<SyntaxTreeNode> optionalChild() {
        if (children.size() > 1) {
            throw new SyntaxTreeException(String.format("%s has multiple children!", value));
        } else {
            return children.stream().findFirst();
        }
    }

    public SyntaxTreeNode child() {
        return  optionalChild().orElseThrow(
            () -> new SyntaxTreeException(String.format("%s does not have children!", value))
        );
    }

    public Optional<String> optionalChildValue() {
        return optionalChild().map(SyntaxTreeNode::value);
    }

    public String childValue() {
        return child().value;
    }

    public Optional<String> optionalSingular(String childValue) {
        return optionalChild(childValue).map(SyntaxTreeNode::child).map(SyntaxTreeNode::value);
    }

    public String singular(String childValue) {
        return child(childValue).childValue();
    }
}
