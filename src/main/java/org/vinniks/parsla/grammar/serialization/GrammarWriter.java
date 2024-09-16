package org.vinniks.parsla.grammar.serialization;

import lombok.NonNull;
import org.vinniks.parsla.grammar.Grammar;

import java.io.IOException;
import java.io.Writer;

public interface GrammarWriter {
    void write(@NonNull Grammar grammar, @NonNull Writer writer) throws IOException;
}
