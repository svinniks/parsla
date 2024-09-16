package org.vinniks.parsla.grammar.serialization;

import org.vinniks.parsla.exception.GrammarException;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.parser.text.TextParser;
import org.vinniks.parsla.tokenizer.text.buffered.CharacterBufferProvider;

import java.io.IOException;
import java.io.Reader;

import static org.vinniks.parsla.grammar.GrammarBuilder.grammar;
import static org.vinniks.parsla.grammar.GrammarBuilder.items;
import static org.vinniks.parsla.grammar.GrammarBuilder.option;
import static org.vinniks.parsla.grammar.GrammarBuilder.options;
import static org.vinniks.parsla.grammar.GrammarBuilder.rule;
import static org.vinniks.parsla.grammar.GrammarBuilder.token;

public final class StandardGrammarReader implements GrammarReader {
    private static final int DEFAULT_CHARACTER_BUFFER_SIZE = 8 * 1024;
    private static final StandardGrammarReader INSTANCE = new StandardGrammarReader(() -> new char[DEFAULT_CHARACTER_BUFFER_SIZE]);

    public static StandardGrammarReader instance() {
        return INSTANCE;
    }

    public static Grammar grammarGrammar() {
        return grammar(options(
            option("options"),
            option("options", items(rule("option"), rule("options"))),
            option("option", true, items(rule("output"), rule("rule-name", true), token("colon"), rule("items", true), token("semicolon"))),
            option("output"),
            option("output", true, items(token("gt"))),
            option("rule-name", items(token("identifier", false, true))),
            option("items", items(token("caret"))),
            option("items", items(rule("item"), rule("items-tail"))),
            option("items-tail"),
            option("items-tail", items(rule("item"), rule("items-tail"))),
            option("item", items(rule("token"))),
            option("item", items(rule("rule"))),
            option("token", true, items(token("left-curly-bracket"), rule("token-type"), rule("token-value"), token("right-curly-bracket"), rule("elevations", true))),
            option("token-type", items(rule("output-type"), rule("type"))),
            option("output-type"),
            option("output-type", true, items(token("gt"))),
            option("type"),
            option("type", true, items(token("identifier", false, true))),
            option("token-value"),
            option("token-value", items(token("comma"), rule("output-value"), rule("value"))),
            option("output-value"),
            option("output-value", true, items(token("gt"))),
            option("value"),
            option("value", true, items(token("string", false, true))),
            option("elevations"),
            option("elevations", items(rule("elevation", true), rule("elevations"))),
            option("elevation", items(token("exclamation"))),
            option("rule", true, items(rule("output"), rule("name", true))),
            option("name", items(token("identifier", false, true)))
        ));
    }

    private final TextParser parser;

    public StandardGrammarReader(CharacterBufferProvider characterBufferProvider) {
        parser = new TextParser(
            grammarGrammar(),
            new GrammarTokenizer(
                false,
                new StandardIdentifierCharacterValidator(),
                characterBufferProvider
            )
        );
    }

    @Override
    public Grammar read(Reader reader) throws IOException {
        try {
            var syntaxTree = parser.parse(reader, "options");
            return StandardGrammarBuilder.build(syntaxTree);
        } catch (ParsingException e) {
            throw new GrammarException("failed to read standard grammar", e);
        }
    }
}
