package org.vinniks.parsla.grammar;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.vinniks.parsla.grammar.serialization.ExtendedGrammarReader;
import org.vinniks.parsla.grammar.serialization.StandardGrammarReader;
import org.vinniks.parsla.grammar.serialization.StandardGrammarWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import static org.vinniks.parsla.util.Validations.requireNonNullElements;

@Getter
public final class Grammar {
    public static Grammar readStandard(Reader source) throws IOException {
        return StandardGrammarReader.instance().read(source);
    }

    public static Grammar readStandard(String source) {
        return StandardGrammarReader.instance().read(source);
    }

    public static Grammar readExtended(Reader source) throws IOException {
        return ExtendedGrammarReader.instance().read(source);
    }

    public static Grammar readExtended(String source) {
        return ExtendedGrammarReader.instance().read(source);
    }

    private final Iterable<Option> options;

    public Grammar(@NonNull Iterable<Option> options) {
        requireNonNullElements(options, "grammar option can not be null");
        this.options = options;
    }

    @Override
    @SneakyThrows
    public String toString() {
        try (var writer = new StringWriter()) {
            StandardGrammarWriter.instance().write(this, writer);
            return writer.toString();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Grammar otherGrammar) {
            var optionIterator = options.iterator();
            var otherOptionIterator = otherGrammar.options.iterator();

            while (optionIterator.hasNext() && otherOptionIterator.hasNext()) {
                if (!optionIterator.next().equals(otherOptionIterator.next())) {
                    return false;
                }
            }

            return !optionIterator.hasNext() && !otherOptionIterator.hasNext();
        } else {
            return false;
        }
    }
}
