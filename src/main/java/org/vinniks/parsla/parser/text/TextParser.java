package org.vinniks.parsla.parser.text;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.parser.NoActionListener;
import org.vinniks.parsla.parser.Parser;
import org.vinniks.parsla.parser.ParserOutputListener;
import org.vinniks.parsla.syntaxtree.SyntaxTreeBuilder;
import org.vinniks.parsla.syntaxtree.SyntaxTreeNode;
import org.vinniks.parsla.tokenizer.text.TextPosition;
import org.vinniks.parsla.tokenizer.text.TextTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import static java.util.Collections.emptySet;

@SuppressWarnings("unused")
public class TextParser extends Parser<TextPosition> {
    private final TextTokenizer tokenizer;

    public TextParser(Grammar grammar, Set<String> ignoredTokenTypes, @NonNull TextTokenizer tokenizer) {
        super(grammar, ignoredTokenTypes);
        this.tokenizer = tokenizer;
    }

    public TextParser(Grammar grammar, TextTokenizer tokenizer) {
        this(grammar, emptySet(), tokenizer);
    }

    public void parse(@NonNull Reader source, String rootRuleName, ParserOutputListener<TextPosition> outputListener) throws IOException {
        try (var tokenIterator = tokenizer.getTokenIterator(source)) {
            parse(tokenIterator, rootRuleName, outputListener);
        }
    }

    @SneakyThrows
    public void parse(@NonNull String source, String rootRuleName, ParserOutputListener<TextPosition> outputListener) {
        try (var reader = new StringReader(source)) {
            parse(reader, rootRuleName, outputListener);
        }
    }

    public SyntaxTreeNode<TextPosition> parse(Reader source, String rootRuleName) throws IOException {
        var syntaxTreeBuilder = new SyntaxTreeBuilder<TextPosition>();
        parse(source, rootRuleName, syntaxTreeBuilder);
        return syntaxTreeBuilder.build();
    }

    @SneakyThrows
    public SyntaxTreeNode<TextPosition> parse(@NonNull String source, String rootRuleName) {
        try (var reader = new StringReader(source)) {
            return parse(reader, rootRuleName);
        }
    }

    public void validate(Reader source, String rootRuleName) throws IOException {
        parse(source, rootRuleName, new NoActionListener<>());
    }

    public void validate(String source, String rootRuleName) {
        parse(source, rootRuleName, new NoActionListener<>());
    }
}
