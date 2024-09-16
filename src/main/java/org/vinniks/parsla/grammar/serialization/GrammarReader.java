package org.vinniks.parsla.grammar.serialization;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.vinniks.parsla.grammar.Grammar;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public interface GrammarReader {
    Grammar read(Reader source) throws IOException;

    @SneakyThrows
    default Grammar read(@NonNull String source) {
        try (var reader = new StringReader(source)) {
            return read(reader);
        }
    }
}
