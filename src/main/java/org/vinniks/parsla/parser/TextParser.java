package org.vinniks.parsla.parser;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.syntaxtree.SyntaxTreeBuilder;
import org.vinniks.parsla.syntaxtree.SyntaxTreeNode;
import org.vinniks.parsla.tokenizer.TextTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;

public class TextParser extends Parser {
    private final TextTokenizer textTokenizer;

    public TextParser(Grammar grammar, Set<String> ignoredTokenTypes, TextTokenizer textTokenizer) {
        super(grammar, ignoredTokenTypes, createSupportedTokenTypeMap(textTokenizer)::get);
        this.textTokenizer = textTokenizer;
    }

    public TextParser(Grammar grammar, TextTokenizer textTokenizer) {
        this(grammar, emptySet(), textTokenizer);
    }

    public void parse(@NonNull Reader source, String rootRuleName, ParserOutputListener outputListener) throws IOException {
        try (var tokenIterator = textTokenizer.getTokenIterator(source)) {
            parse(tokenIterator, rootRuleName, outputListener);
        }
    }

    @SneakyThrows
    public void parse(@NonNull String source, String rootRuleName, ParserOutputListener outputListener) {
        try (var reader = new StringReader(source)) {
            parse(reader, rootRuleName, outputListener);
        }
    }

    public SyntaxTreeNode parse(Reader source, String rootRuleName) throws IOException {
        var syntaxTreeBuilder = new SyntaxTreeBuilder();
        parse(source, rootRuleName, syntaxTreeBuilder);
        return syntaxTreeBuilder.build();
    }

    @SneakyThrows
    public SyntaxTreeNode parse(@NonNull String source, String rootRuleName) {
        try (var reader = new StringReader(source)) {
            return parse(reader, rootRuleName);
        }
    }

    public void validate(Reader source, String rootRuleName) throws IOException {
        parse(source, rootRuleName, new NoActionListener());
    }

    public void validate(String source, String rootRuleName) {
        parse(source, rootRuleName, new NoActionListener());
    }

    private static Map<String, String> createSupportedTokenTypeMap(TextTokenizer textTokenizer) {
        return StreamSupport
            .stream(textTokenizer.getTokenTypes().spliterator(), false)
            .collect(Collectors.toMap(identity(), identity()));
    }
}
