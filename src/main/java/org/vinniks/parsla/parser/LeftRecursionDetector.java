package org.vinniks.parsla.parser;

import lombok.AccessLevel;
import lombok.Getter;
import org.vinniks.parsla.exception.GrammarException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class LeftRecursionDetector {
    static void detect(Map<String, Collection<CompiledOption>> compiledRules) {
        new LeftRecursionDetector(compiledRules).detect();
    }

    private final Map<String, Collection<CompiledOption>> compiledRules;
    private final Set<CompiledOption> visitedOptions;

    private LeftRecursionDetector(Map<String, Collection<CompiledOption>> compiledRules){
        this.compiledRules = compiledRules;
        visitedOptions = new LinkedHashSet<>();
    }

    private void detect() {
        compiledRules.keySet().forEach(ruleName -> detect(ruleName, new RecursionTreeNode(null, ruleName)));
    }

    private RecursionTreeNode detect(String ruleName, RecursionTreeNode path) {
        RecursionTreeNode shortestEmptyPath = null;

        for (var option : compiledRules.get(ruleName)) {
            var emptyPath = detect(option, path);

            if (shortestEmptyPath == null || emptyPath != null && shortestEmptyPath.getLevel() > emptyPath.getLevel()) {
                shortestEmptyPath = emptyPath;
            }
        }

        return shortestEmptyPath;
    }

    private RecursionTreeNode detect(CompiledOption option, RecursionTreeNode path) {
        if (visitedOptions.contains(option)) {
            throw new GrammarException(String.format("Left recursion detected at %s", buildRecursionPath(path)));
        }

        visitedOptions.add(option);

        try {
            for (var item : option.getItems(false)) {
                path = item instanceof CompiledRuleItem ruleItem
                    ? detect(ruleItem.getRuleName(), new RecursionTreeNode(path, ruleItem.getRuleName()))
                    : null;

                if (path == null) {
                    break;
                }
            }
        } finally {
            visitedOptions.remove(option);
        }

        return path;
    }

    private String buildRecursionPath(RecursionTreeNode path) {
        var builder = new StringBuilder();
        buildRecursionPath(path, builder, null);
        return builder.toString();
    }

    private void buildRecursionPath(RecursionTreeNode path, StringBuilder builder, String recursiveRuleName) {
        if (path.getParent() != null && recursiveRuleName == null || !recursiveRuleName.equals(path.getRuleName())) {
            buildRecursionPath(path.getParent(), builder, recursiveRuleName == null ? path.getRuleName() : recursiveRuleName);
        }

        if (!builder.isEmpty()) {
            builder.append(" > ");
        }

        builder.append(path.getRuleName());
    }

    @Getter(AccessLevel.PRIVATE)
    private static class RecursionTreeNode {
        private final RecursionTreeNode parent;
        private final String ruleName;
        private final int level;

        public RecursionTreeNode(RecursionTreeNode parent, String ruleName) {
            this.parent = parent;
            this.ruleName = ruleName;
            level = parent == null ? 1 : parent.getLevel() + 1;
        }
    }
}
