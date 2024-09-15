package org.vinniks.parsla.parser;

import lombok.*;
import org.vinniks.parsla.exception.GrammarException;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.grammar.RuleItem;
import org.vinniks.parsla.grammar.TokenItem;
import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.TokenIterator;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptySet;

public class Parser {
    @Getter
    private final Grammar grammar;

    @Getter
    private final Set<String> ignoredTokenTypes;

    private Map<String, Collection<CompiledOption>> compiledRules;
    private CompiledRuleItem ignoredTokenRuleItem;

    protected Parser(@NonNull Grammar grammar, @NonNull Set<String> ignoredTokenTypes, Function<String, String> supportedTokenTypeFn) {
        if (ignoredTokenTypes.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Ignored token types must not contain nulls");
        }

        this.grammar = grammar;
        this.ignoredTokenTypes = Set.copyOf(ignoredTokenTypes);
        compileRules(supportedTokenTypeFn);
        createIgnoredTokenRuleItem();
        LeftRecursionDetector.detect(compiledRules);
    }

    public Parser(@NonNull Grammar grammar, @NonNull Set<String> ignoredTokenTypes) {
        this(grammar, ignoredTokenTypes, Function.identity());
    }

    public Parser(Grammar grammar) {
        this(grammar, emptySet());
    }

    public final void parse(
        @NonNull TokenIterator tokenIterator,
        @NonNull String rootRuleName,
        @NonNull ParserOutputListener outputListener
    ) throws IOException {
        var rootItem = new CompiledRuleItem(rootRuleName, true, getOptions(rootRuleName));
        var paths = new ArrayList<Path<?>>();
        var ignoredTokenRuleNode = ignoredTokenRuleItem != null ? new RuleLookAheadTreeNode(null, ignoredTokenRuleItem, 2) : null;
        paths.add(new Path<>(null, new RuleLookAheadTreeNode(ignoredTokenRuleNode, rootItem,1)));
        var nextPaths = new ArrayList<Path<TokenParseTreeNode>>();
        var output = new ParserOutput(outputListener);

        while (tokenIterator.hasNext()) {
            var token = tokenIterator.next();
            nextPaths.clear();
            paths.forEach(path -> findNextPaths(path, token, nextPaths));
            paths.clear();

            if (nextPaths.isEmpty()) {
                throw new ParsingException(String.format("Unexpected %s", token));
            } else if (nextPaths.size() == 1) {
                var nextPath = nextPaths.getFirst();
                output.next(nextPath.getParseTreeNode());

                if (nextPath.getLookAheadTreeNode() != null) {
                    paths.add(new Path<>(null, nextPath.getLookAheadTreeNode()));
                }
            } else {
                paths.addAll(nextPaths);
            }
        }

        if (!paths.isEmpty()) {
            var tailPaths = new ArrayList<Path<?>>();
            paths.forEach(path -> findTails(path, tailPaths));

            if (tailPaths.isEmpty()) {
                throw new ParsingException("Unexpected end of the input");
            } else if (tailPaths.size() > 1) {
                throw new ParsingException("Ambiguous parsing path detected");
            } else {
                output.next(tailPaths.getFirst().getParseTreeNode());
            }
        }

        output.end();
    }



    private void compileRules(Function<String, String> supportedTokenTypeFn) {
        compiledRules = new LinkedHashMap<>();

        grammar.getOptions().forEach(option -> {
            if (!compiledRules.containsKey(option.getRuleName())) {
                compiledRules.put(option.getRuleName(), new ArrayList<>());
            }
        });

        grammar.getOptions().forEach(option -> {
            var compiledItems = StreamSupport
                .stream(option.getItems().spliterator(), false)
                .map(item -> switch (item) {
                    case TokenItem tokenItem -> new CompiledRegularTokenItem(
                        tokenItem.getElevation(),
                        Optional
                            .ofNullable(supportedTokenTypeFn.apply(tokenItem.getTokenType()))
                            .orElse(tokenItem.getTokenType()),
                        tokenItem.isOutputType(),
                        tokenItem.getTokenValue(),
                        tokenItem.isOutputValue()
                    );
                    case RuleItem ruleItem -> new CompiledRuleItem(
                        ruleItem.getRuleName(),
                        ruleItem.isOutput(),
                        getOptions(ruleItem.getRuleName())
                    );
                })
                .toArray(CompiledItem[]::new);

            compiledRules
                .get(option.getRuleName())
                .add(new CompiledOption(option.isOutput(), compiledItems));
        });
    }

    private void createIgnoredTokenRuleItem() {
        if (!ignoredTokenTypes.isEmpty()) {
            var ignoredTokenOptions = new ArrayList<CompiledOption>();
            ignoredTokenRuleItem = new CompiledRuleItem(null, false, ignoredTokenOptions);
            ignoredTokenOptions.add(new CompiledOption(false, new CompiledItem[0]));

            ignoredTokenOptions.add(new CompiledOption(
                false, new CompiledItem[]{new CompiledIgnoredTokenItem(), ignoredTokenRuleItem}
            ));
        }
    }

    private Collection<CompiledOption> getOptions(String ruleName) {
        return Optional
            .ofNullable(compiledRules.get(ruleName))
            .orElseThrow(() -> new GrammarException(String.format("Unknown grammar rule \"%s\"", ruleName)));
    }

    private void findNextPaths(Path<?> path, Token token, List<Path<TokenParseTreeNode>> nextPaths) {
        if (path.getLookAheadTreeNode() != null) {
            switch (path.getLookAheadTreeNode()) {
                case RuleLookAheadTreeNode ruleNode -> ruleNode
                    .explode(path.getParseTreeNode())
                    .forEach(explodedPath -> findNextPaths(explodedPath, token, nextPaths));

                case TokenLookAheadTreeNode tokenNode -> {
                    var match = tokenNode.getItem().match(token, ignoredTokenTypes);

                    if (match > 0) {
                        var nextPath = tokenNode.save(path.getParseTreeNode(), token, match);

                        if (!nextPaths.isEmpty() && nextPaths.getFirst().getParseTreeNode().getMatch() < match) {
                            nextPaths.clear();
                        }

                        if (nextPaths.isEmpty() || nextPaths.getFirst().getParseTreeNode().getMatch() == match) {
                            nextPaths.add(nextPath);
                        }
                    }
                }
            }
        }
    }

    private void findTails(Path<?> path, Collection<Path<?>> tailPaths) {
        if (path.getLookAheadTreeNode() == null) {
            tailPaths.add(path);
        } else if (path.getLookAheadTreeNode() instanceof RuleLookAheadTreeNode ruleNode) {
            ruleNode.explode(path.getParseTreeNode()).forEach(explodedPath -> findTails(explodedPath, tailPaths));
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(AccessLevel.PROTECTED)
    sealed abstract class AbstractLookAheadTreeNode<T extends CompiledItem> permits RuleLookAheadTreeNode, TokenLookAheadTreeNode {
        private final AbstractLookAheadTreeNode<?> parent;
        private final T item;
        private final int level;
    }

    final class TokenLookAheadTreeNode extends AbstractLookAheadTreeNode<AbstractCompiledTokenItem> {
        private TokenLookAheadTreeNode(AbstractLookAheadTreeNode<?> parent, AbstractCompiledTokenItem item, int level) {
            super(parent, item, level);
        }

        private Path<TokenParseTreeNode> save(AbstractParseTreeNode<?> parentParseTreeNode, Token token, int match) {
            return new Path<>(
                new TokenParseTreeNode(parentParseTreeNode, getLevel(), getItem(), token, match),
                getParent()
            );
        }
    }

    final class RuleLookAheadTreeNode extends AbstractLookAheadTreeNode<CompiledRuleItem> {
        private RuleLookAheadTreeNode(AbstractLookAheadTreeNode<?> parent, CompiledRuleItem item, int level) {
            super(parent, item, level);
        }

        private Stream<Path<?>> explode(AbstractParseTreeNode<?> parentParseTreeNode) {
            return getItem()
                .getOptions()
                .stream()
                .map(option -> {
                    var explodedNode = getParent();

                    for (var item : option.getItems(true)) {
                        explodedNode = switch (item) {
                            case AbstractCompiledTokenItem tokenItem -> new TokenLookAheadTreeNode(explodedNode, tokenItem, getLevel() + 1);
                            case CompiledRuleItem ruleItem -> new RuleLookAheadTreeNode(explodedNode, ruleItem, getLevel() + 1);
                        };

                        if (item instanceof CompiledRegularTokenItem && ignoredTokenRuleItem != null) {
                            explodedNode = new RuleLookAheadTreeNode(explodedNode, ignoredTokenRuleItem, getLevel() + 1);
                        }
                    }

                    return new Path<>(
                        new RuleParseTreeNode(
                            parentParseTreeNode,
                            getLevel(),
                            getItem(),
                            option.isOutput()
                        ),
                        explodedNode
                    );
                });
        }
    }
}

