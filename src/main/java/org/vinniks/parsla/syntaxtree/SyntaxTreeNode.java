package org.vinniks.parsla.syntaxtree;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.vinniks.parsla.exception.SyntaxTreeException;
import org.vinniks.parsla.syntaxtree.serialization.DefaultSyntaxTreeWriter;

import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class SyntaxTreeNode<P> {
    private final P position;
    private final String value;
    private final List<SyntaxTreeNode<P>> children;

    public P position() {
        return position;
    }

    public String value() {
        return value;
    }

    public List<SyntaxTreeNode<P>> children() {
        return children;
    }

    @Override
    @SneakyThrows
    public String toString() {
        try (var writer = new StringWriter()) {
            new DefaultSyntaxTreeWriter(true).write(this, writer);
            return writer.toString();
        }
    }

    public boolean valueIs(String value) {
        return this.value.equals(value);
    }

    public Optional<SyntaxTreeNode<P>> optionalChild(String childValue) {
        var matchingChildren = children
            .stream()
            .filter(childNode -> childNode.value.equals(childValue))
            .toList();

        if (matchingChildren.size() > 1) {
            throw new SyntaxTreeException(String.format("%s has multiple %s", value, childValue));
        } else {
            return matchingChildren.stream().findFirst();
        }
    }

    public boolean hasChild(String childValue) {
        return optionalChild(childValue).isPresent();
    }

    public SyntaxTreeNode<P> child(String childValue) {
        return optionalChild(childValue).orElseThrow(
            () -> new SyntaxTreeException(String.format("%s does not contain %s", value, childValue))
        );
    }

    public Optional<SyntaxTreeNode<P>> optionalChild() {
        if (children.size() > 1) {
            throw new SyntaxTreeException(String.format("%s has multiple children", value));
        } else {
            return children.stream().findFirst();
        }
    }

    public SyntaxTreeNode<P> child() {
        return optionalChild().orElseThrow(
            () -> new SyntaxTreeException(String.format("%s does not have children", value))
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
