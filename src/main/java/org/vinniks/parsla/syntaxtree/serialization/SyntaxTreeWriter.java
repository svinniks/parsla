package org.vinniks.parsla.syntaxtree.serialization;

import org.vinniks.parsla.syntaxtree.SyntaxTreeNode;

import java.io.IOException;
import java.io.Writer;

public interface SyntaxTreeWriter {
    void write(SyntaxTreeNode<?> syntaxTreeNode, Writer writer) throws IOException;
}
