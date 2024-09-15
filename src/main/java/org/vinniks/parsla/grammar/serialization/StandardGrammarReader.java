package org.vinniks.parsla.grammar.serialization;

import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.grammar.Item;
import org.vinniks.parsla.grammar.Option;
import org.vinniks.parsla.parser.Parser;
import org.vinniks.parsla.parser.TextParser;
import org.vinniks.parsla.syntaxtree.SyntaxTreeNode;

import java.io.IOException;
import java.io.Reader;

import static org.vinniks.parsla.grammar.GrammarBuilder.*;

public class StandardGrammarReader implements GrammarReader {
    private static final TextParser PARSER;

    static {
        var grammar = grammar(options(
            option("options"),
            option("options", items(rule("option"), rule("options"))),
            option("option", true, items(rule("output"), rule("rule-name", true), token("colon"), rule("items", true), token("semicolon"))),
            option("output"),
            option("output", true, items(token("gt"))),
            option("rule-name", items(token("identifier", false, true))),
            option("items", items(token("empty"))),
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

        PARSER = new TextParser(grammar, new GrammarTokenizer(false));
    }

    @Override
    public Grammar read(Reader reader) throws IOException {
        var syntaxTree = PARSER.parse(reader, "options");
        return buildGrammar(syntaxTree);
    }

    public Parser getParser() {
        return PARSER;
    }

    private Grammar buildGrammar(SyntaxTreeNode rootNode) {
        return grammar(rootNode
            .children()
            .stream()
            .map(this::buildOption)
            .toList()
        );
    }

    private Option buildOption(SyntaxTreeNode optionNode) {
        return option(
            optionNode.singular("rule-name"),
            optionNode.hasChild("output"),
            optionNode
                .child("items")
                .children()
                .stream()
                .map(this::buildItem)
                .toList()
        );
    }

    private Item buildItem(SyntaxTreeNode itemNode) {
        return itemNode.valueIs("rule")
            ? rule(itemNode.singular("name"), itemNode.hasChild("output"))
            : token(
                itemNode.child("elevations").children().size(),
                itemNode.optionalSingular("type").orElse(null),
                itemNode.hasChild("output-type"),
                itemNode.optionalSingular("value").orElse(null),
                itemNode.hasChild("output-value")
            );
    }
}
