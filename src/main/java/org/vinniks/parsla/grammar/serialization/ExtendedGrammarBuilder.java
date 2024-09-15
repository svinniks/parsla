package org.vinniks.parsla.grammar.serialization;

import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.grammar.Item;
import org.vinniks.parsla.grammar.Option;
import org.vinniks.parsla.syntaxtree.SyntaxTreeNode;

import java.util.*;

import static org.vinniks.parsla.grammar.GrammarBuilder.*;

class ExtendedGrammarBuilder {
    static Grammar build(SyntaxTreeNode syntaxTree) {
        return new ExtendedGrammarBuilder(syntaxTree).build();
    }

    private final SyntaxTreeNode syntaxTree;
    private final Collection<Option> options;
    private final Map<String, Integer> subOptionCounters;

    private ExtendedGrammarBuilder(SyntaxTreeNode syntaxTree) {
        this.syntaxTree = syntaxTree;
        options = new ArrayList<>();
        subOptionCounters = new HashMap<>();
    }

    private Grammar build() {
        syntaxTree.children().forEach(this::buildOption);
        return grammar(options);
    }

    private void buildOption(SyntaxTreeNode optionNode) {
        var ruleName = optionNode.singular("rule-name");
        var output = optionNode.hasChild("output");

        optionNode
            .child("sequences")
            .children()
            .stream()
            .map(sequenceNode -> buildSequence(ruleName, output, sequenceNode))
            .forEach(options::add);
    }

    private Option buildSequence(String ruleName, boolean output, SyntaxTreeNode sequenceNode) {
        return sequenceNode.valueIs("empty")
            ? option(ruleName, output)
            : option(
                ruleName,
                output,
                sequenceNode
                    .children()
                    .stream()
                    .map(itemNode -> buildItem(ruleName, itemNode))
                    .toList()
        );
    }

    private Item buildItem(String ruleName, SyntaxTreeNode itemNode) {
        var quantifier = itemNode.optionalSingular("quantifier").orElse("one");
        var item = buildBody(ruleName, itemNode.child("body"));

        if (quantifier.equals("one")) {
            return item;
        } else {
            var subOptionRuleName = generateSubOptionRuleName(ruleName);
            var subOptionRuleItem = rule(subOptionRuleName);

            if (quantifier.startsWith("zero")) {
                options.add(option(subOptionRuleName));
            }

            var subOptionItems = new ArrayList<Item>();
            subOptionItems.add(item);

            if (quantifier.equals("zero-or-many")) {
                subOptionItems.add(rule(subOptionRuleName));
            } else if (quantifier.equals("one-or-many")) {
                var tailOptionRuleName = String.format("%s_", subOptionRuleName);
                subOptionItems.add(rule(tailOptionRuleName));
                options.add(option(tailOptionRuleName));
                options.add(option(tailOptionRuleName, List.of(item, rule(tailOptionRuleName))));
            }

            options.add(option(subOptionRuleName, subOptionItems));
            return subOptionRuleItem;
        }
    }

    private Item buildBody(String ruleName, SyntaxTreeNode bodyNode) {
        var contentNode = bodyNode.child();

        if (contentNode.valueIs("token")) {
            return token(
                contentNode.child("elevations").children().size(),
                contentNode.optionalSingular("type").orElse(null),
                contentNode.hasChild("output-type"),
                contentNode.optionalSingular("value").orElse(null),
                contentNode.hasChild("output-value")
            );
        } else if (contentNode.valueIs("rule")) {
            return rule(contentNode.singular("name"), contentNode.hasChild("output"));
        } else {
            var subOptionRuleName = generateSubOptionRuleName(ruleName);

            contentNode
                .child("sequences")
                .children()
                .stream()
                .map(sequenceNode -> buildSequence(subOptionRuleName, false, sequenceNode))
                .forEach(options::add);

            return rule(subOptionRuleName);
        }
    }

    private String generateSubOptionRuleName(String ruleName) {
        var subOptionNumber = subOptionCounters.compute(ruleName, (key, value) -> value == null ? 1 : value + 1);
        return String.format("%s#%d", ruleName, subOptionNumber);
    }
}
