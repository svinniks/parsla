package org.vinniks.parsla.grammar.serialization;

import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.parser.Parser;
import org.vinniks.parsla.parser.TextParser;

import java.io.IOException;
import java.io.Reader;

public class ExtendedGrammarReader implements GrammarReader {
    private static final TextParser PARSER;

    static {
        var grammar = Grammar.readStandard("""
            options: ^;
            options: >option options;
            
            option: output >rule-name {colon} >sequences {semicolon};
            
            output: ^;
            >output: {gt};
            
            rule-name: {identifier, >};

            sequences: sequence sequences-tail;
            
            sequence: {>empty};
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
            
            zero-or-one: {question-mark};
            zero-or-many: {asterisk};
            one-or-many: {plus};
            
            items-tail: ^;
            items-tail: item items-tail;
            """);

        PARSER = new TextParser(grammar, new GrammarTokenizer(true));
    }

    @Override
    public Grammar read(Reader reader) throws IOException {
        var syntaxTree = PARSER.parse(reader, "options");
        return ExtendedGrammarBuilder.build(syntaxTree);
    }

    public Parser getParser() {
        return PARSER;
    }
}
