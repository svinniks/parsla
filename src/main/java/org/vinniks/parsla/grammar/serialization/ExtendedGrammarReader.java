package org.vinniks.parsla.grammar.serialization;

import org.vinniks.parsla.exception.GrammarException;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.parser.text.TextParser;
import org.vinniks.parsla.tokenizer.text.buffered.CharacterBufferProvider;

import java.io.IOException;
import java.io.Reader;

public class ExtendedGrammarReader implements GrammarReader {
    private static final int DEFAULT_CHARACTER_BUFFER_SIZE = 8 * 1024;
    private static final ExtendedGrammarReader INSTANCE = new ExtendedGrammarReader(() -> new char[DEFAULT_CHARACTER_BUFFER_SIZE]);

    public static ExtendedGrammarReader instance() {
        return INSTANCE;
    }

    public static Grammar grammarGrammar() {
        return Grammar.readStandard("""
            options: ^;
            options: >option options;
            
            option: output >rule-name {colon} >sequences {semicolon};
            
            output: ^;
            >output: {gt};
            
            rule-name: {identifier, >};

            sequences: sequence sequences-tail;
            
            sequence: {>caret};
            >sequence: item items-tail;
            
            >item: >body quantifier;
            
            body: {left-bracket} >sub-option {right-bracket};
            body: >token;
            body: >rule;
            
            sub-option: >sequences;
            
            sequences-tail: ^;
            sequences-tail: {pipe} sequence sequences-tail;
            
            token: {left-curly-bracket} token-type token-value {right-curly-bracket} >elevations;
            
            token-type: output-type type;
            
            output-type: ^;
            >output-type: {gt};
            
            type: ^;
            >type: {identifier, >};
            
            token-value: ^;
            token-value: {comma} output-value value;
            
            output-value: ^;
            >output-value: {gt};
            
            value: ^;
            >value: {string, >};
            
            elevations: ^;
            elevations: >elevation elevations;
            
            elevation: {exclamation};
            
            rule: output >name;
            
            name: {identifier, >};
            
            quantifier: ^;
            >quantifier: >zero-or-one;
            >quantifier: >zero-or-many;
            >quantifier: >one-or-many;
            
            zero-or-one: {question};
            zero-or-many: {asterisk};
            one-or-many: {plus};
            
            items-tail: ^;
            items-tail: item items-tail;
            """);
    }

    private final TextParser parser;

    public ExtendedGrammarReader(CharacterBufferProvider characterBufferProvider) {
        parser = new TextParser(
            grammarGrammar(),
            new GrammarTokenizer(
            true,
                new StandardIdentifierCharacterValidator(),
                characterBufferProvider
            )
        );
    }

    @Override
    public Grammar read(Reader reader) throws IOException {
        try {
            var syntaxTree = parser.parse(reader, "options");
            return ExtendedGrammarBuilder.build(syntaxTree);
        } catch (ParsingException e) {
            throw new GrammarException("failed to read extended grammar", e);
        }
    }
}
