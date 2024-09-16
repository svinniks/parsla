package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.vinniks.parsla.exception.GrammarException;
import org.vinniks.parsla.exception.ParsingException;
import org.vinniks.parsla.grammar.Grammar;
import org.vinniks.parsla.grammar.RuleItem;
import org.vinniks.parsla.grammar.TokenItem;
import org.vinniks.parsla.tokenizer.Token;
import org.vinniks.parsla.tokenizer.TokenIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptySet;

@SuppressWarnings("unused")
public class Parser<P> {
    @Getter
    private final Grammar grammar;

    @Getter
    private final Set<String> ignoredTokenTypes;

    private Map<String, Collection<CompiledOption>> compiledRules;
    private CompiledRuleItem ignoredTokenRuleItem;

    protected Parser(@NonNull Grammar grammar, @NonNull Set<String> ignoredTokenTypes) {
        if (ignoredTokenTypes.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Ignored token types must not contain nulls");
        }

        this.grammar = grammar;
        this.ignoredTokenTypes = Set.copyOf(ignoredTokenTypes);
        compileRules();
        createIgnoredTokenRuleItem();
        LeftRecursionDetector.detect(compiledRules);
    }

    public Parser(Grammar grammar) {
        this(grammar, emptySet());
    }

    public final void parse(
        @NonNull TokenIterator<P> tokenIterator,
        @NonNull String rootRuleName,
        @NonNull ParserOutputListener<P> outputListener
    ) throws IOException {
        var rootItem = new CompiledRuleItem(rootRuleName, true, getOptions(rootRuleName));
        var paths = new ArrayList<Path<?>>();
        var ignoredTokenRuleNode = ignoredTokenRuleItem != null ? new RuleLookAheadTreeNode(null, ignoredTokenRuleItem, 2) : null;
        paths.add(new Path<>(null, new RuleLookAheadTreeNode(ignoredTokenRuleNode, rootItem,1)));
        var nextPaths = new ArrayList<Path<TokenParseTreeNode<P>>>();
        var output = new ParserOutput<>(outputListener);

        while (tokenIterator.hasNext()) {
            var token = tokenIterator.next();
            nextPaths.clear();
            paths.forEach(path -> findNextPaths(path, token, tokenIterator.position(), nextPaths));
            paths.clear();

            if (nextPaths.isEmpty()) {
                throw new ParsingException(String.format("unexpected %s", token), tokenIterator.position());
            } else if (nextPaths.size() == 1) {
                var nextPath = nextPaths.get(0);
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
            paths.forEach(path -> findTails(path, tailPaths, tokenIterator.position()));

            if (tailPaths.isEmpty()) {
                throw new ParsingException("unexpected end of the input", tokenIterator.position());
            } else if (tailPaths.size() > 1) {
                throw new ParsingException("Ambiguous parsing path detected", tokenIterator.position());
            } else {
                output.next(tailPaths.get(0).getParseTreeNode());
            }
        }

        output.end();
    }

    private void compileRules() {
        compiledRules = new LinkedHashMap<>();

        grammar.getOptions().forEach(option -> {
            if (!compiledRules.containsKey(option.getRuleName())) {
                compiledRules.put(option.getRuleName(), new ArrayList<>());
            }
        });

        grammar.getOptions().forEach(option -> {
            var compiledItems = StreamSupport
                .stream(option.getItems().spliterator(), false)
                .map(item -> {
                    if (item instanceof TokenItem tokenItem) {
                        return new CompiledRegularTokenItem(
                            tokenItem.getElevation(),
                            tokenItem.getTokenType(),
                            tokenItem.isOutputType(),
                            tokenItem.getTokenValue(),
                            tokenItem.isOutputValue()
                        );
                    } else {
                        var ruleItem = (RuleItem) item;

                        return new CompiledRuleItem(
                            ruleItem.getRuleName(),
                            ruleItem.isOutput(),
                            getOptions(ruleItem.getRuleName())
                        );
                    }
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

    private void findNextPaths(Path<?> path, Token token, P position, List<Path<TokenParseTreeNode<P>>> nextPaths) {
        if (path.getLookAheadTreeNode() != null) {
            if (path.getLookAheadTreeNode() instanceof Parser<?>.RuleLookAheadTreeNode) {
                @SuppressWarnings("unchecked")
                var ruleNode = (RuleLookAheadTreeNode) path.getLookAheadTreeNode();

                ruleNode
                    .explode(path.getParseTreeNode(), position)
                    .forEach(explodedPath -> findNextPaths(explodedPath, token, position, nextPaths));
            } else if (path.getLookAheadTreeNode() instanceof Parser<?>.TokenLookAheadTreeNode) {
                @SuppressWarnings("unchecked")
                var tokenNode = (TokenLookAheadTreeNode) path.getLookAheadTreeNode();

                var match = tokenNode.getItem().match(token, ignoredTokenTypes);

                if (match > 0) {
                    var nextPath = tokenNode.save(path.getParseTreeNode(), token, position, match);

                    if (!nextPaths.isEmpty() && nextPaths.get(0).getParseTreeNode().getMatch() < match) {
                        nextPaths.clear();
                    }

                    if (nextPaths.isEmpty() || nextPaths.get(0).getParseTreeNode().getMatch() == match) {
                        nextPaths.add(nextPath);
                    }
                }
            }
        }
    }

    private void findTails(Path<? extends AbstractParseTreeNode<?, P>> path, Collection<Path<?>> tailPaths, P position) {
        if (path.getLookAheadTreeNode() == null) {
            tailPaths.add(path);
        } else if (path.getLookAheadTreeNode() instanceof Parser<?>.RuleLookAheadTreeNode) {
            @SuppressWarnings("unchecked")
            var ruleNode = (RuleLookAheadTreeNode) path.getLookAheadTreeNode();

            ruleNode
                .explode(path.getParseTreeNode(), position)
                .forEach(explodedPath -> findTails(explodedPath, tailPaths, position));
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

        private Path<TokenParseTreeNode<P>> save(
            AbstractParseTreeNode<?, P> parentParseTreeNode, Token token, P position, int match
        ) {
            return new Path<>(
                new TokenParseTreeNode<>(parentParseTreeNode, getLevel(), getItem(), position, token, match),
                getParent()
            );
        }
    }

    final class RuleLookAheadTreeNode extends AbstractLookAheadTreeNode<CompiledRuleItem> {
        private RuleLookAheadTreeNode(AbstractLookAheadTreeNode<?> parent, CompiledRuleItem item, int level) {
            super(parent, item, level);
        }

        private Stream<Path<?>> explode(AbstractParseTreeNode<?, P> parentParseTreeNode, P position) {
            return getItem()
                .getOptions()
                .stream()
                .map(option -> {
                    var explodedNode = getParent();

                    for (var item : option.getItems(true)) {
                        if (item instanceof AbstractCompiledTokenItem tokenItem) {
                            explodedNode = new TokenLookAheadTreeNode(
                                explodedNode, tokenItem, getLevel() + 1
                            );
                        } else {
                            var ruleItem = (CompiledRuleItem) item;

                            explodedNode = new RuleLookAheadTreeNode(
                                explodedNode, ruleItem, getLevel() + 1
                            );
                        }

                        if (item instanceof CompiledRegularTokenItem && ignoredTokenRuleItem != null) {
                            explodedNode = new RuleLookAheadTreeNode(explodedNode, ignoredTokenRuleItem, getLevel() + 1);
                        }
                    }

                    return new Path<>(
                        new RuleParseTreeNode<>(
                            parentParseTreeNode,
                            getLevel(),
                            getItem(),
                            position,
                            option.isOutput()
                        ),
                        explodedNode
                    );
                });
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private class Path<T extends AbstractParseTreeNode<?, P>> {
        private final T parseTreeNode;
        private final AbstractLookAheadTreeNode<?> lookAheadTreeNode;
    }
}

