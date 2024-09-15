package org.vinniks.parsla.grammar.serialization;

import org.vinniks.parsla.grammar.Grammar;

import java.io.IOException;
import java.io.Writer;

public interface GrammarWriter {
    void write(Grammar grammar, Writer writer) throws IOException;
}
