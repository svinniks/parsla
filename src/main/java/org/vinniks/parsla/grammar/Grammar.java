package org.vinniks.parsla.grammar;

import lombok.*;
import org.vinniks.parsla.grammar.serialization.ExtendedGrammarReader;
import org.vinniks.parsla.grammar.serialization.GrammarReader;
import org.vinniks.parsla.grammar.serialization.StandardGrammarReader;
import org.vinniks.parsla.grammar.serialization.StandardGrammarWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class Grammar {
    private static final GrammarReader DEFAULT_READER = new StandardGrammarReader();
    private static final GrammarReader EXTENDED_READER = new ExtendedGrammarReader();

    public static Grammar readStandard(Reader source) throws IOException {
        return DEFAULT_READER.read(source);
    }

    public static Grammar readStandard(String source) {
        return DEFAULT_READER.read(source);
    }

    public static Grammar readExtended(Reader source) throws IOException {
        return EXTENDED_READER.read(source);
    }

    public static Grammar readExtended(String source) {
        return EXTENDED_READER.read(source);
    }

    @NonNull
    @Singular
    private final Iterable<@NonNull Option> options;

    @Override
    @SneakyThrows
    public String toString() {
        try (var writer = new StringWriter()) {
            new StandardGrammarWriter().write(this, writer);
            return writer.toString();
        }
    }
}
