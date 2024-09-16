package org.vinniks.parsla.grammar.serialization;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.grammar.Item;
import org.vinniks.parsla.grammar.Option;
import org.vinniks.parsla.syntaxtree.SyntaxTreeNode;
import org.vinniks.parsla.tokenizer.text.TextPosition;

import static org.vinniks.parsla.grammar.GrammarBuilder.grammar;
import static org.vinniks.parsla.grammar.GrammarBuilder.option;
import static org.vinniks.parsla.grammar.GrammarBuilder.rule;
import static org.vinniks.parsla.grammar.GrammarBuilder.token;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class StandardGrammarBuilder {
    static Grammar build(SyntaxTreeNode<TextPosition> syntaxTree) {
        return new StandardGrammarBuilder(syntaxTree).build();
    }

    private final SyntaxTreeNode<TextPosition> syntaxTree;

    private Grammar build() {
        return grammar(syntaxTree
            .children()
            .stream()
            .map(this::buildOption)
            .toList()
        );
    }

    private Option buildOption(SyntaxTreeNode<TextPosition> optionNode) {
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

    private Item buildItem(SyntaxTreeNode<TextPosition> itemNode) {
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
