package org.vinniks.parsla.syntaxtree.serialization;

import org.vinniks.parsla.syntaxtree.SyntaxTreeNode;

import java.io.IOException;
import java.io.Writer;

public class DefaultSyntaxTreeWriter implements SyntaxTreeWriter {
    @Override
    public void write(SyntaxTreeNode syntaxTreeNode, Writer writer) throws IOException {
        writeNode(syntaxTreeNode, 0, writer);
    }

    private void writeNode(SyntaxTreeNode node, int level, Writer writer) throws IOException {
        for (var i = 0; i < level; i++) {
            writer.append("    ");
        }

        writer
            .append(node.value())
            .append('\n');

        for (var child : node.children()) {
            writeNode(child, level + 1, writer);
        }
    }
}
