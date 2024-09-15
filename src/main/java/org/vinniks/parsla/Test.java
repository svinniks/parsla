package org.vinniks.parsla;

import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.grammar.serialization.GrammarTokenizer;
import org.vinniks.parsla.parser.Parser;
import org.vinniks.parsla.parser.TextParser;
import org.vinniks.parsla.tokenizer.tokenizers.PatternTokenizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws IOException {
        //testIgnored();
        testIgnored();
    }

    private static void testGrammarTokenizer() throws IOException {
        var tokenizer = new GrammarTokenizer(true);
        var tokenIterator = tokenizer.getTokenIterator(new StringReader("""
            word: {>a} {b} (>c)* {space}!* {>a};
            // Hello, World!
            c: >comments {c};
            /*
            Hello, World!
            */
            comments: {>space}!* "abc" """));

        while (tokenIterator.hasNext()) {
            System.out.println(tokenIterator.next());
        }
    }

    private static void testIgnored() {
        var tokenizer = PatternTokenizer
            .builder()
            .pattern("a", "a")
            .pattern("b", "b")
            .pattern("c", "c")
            .pattern("d", "d")
            .pattern("space", "\\s")
            .build();

        var grammar = Grammar.readExtended("""
            word: {>a} ({>space}!? {>b})* {space}!? {>c};
            """);

        var parser = new TextParser(grammar, Set.of("space"), tokenizer);

        System.out.println(parser.parse("""
            a  b         b      c""", "word"));
    }
}
