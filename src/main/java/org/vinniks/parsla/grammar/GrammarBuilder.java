package org.vinniks.parsla.grammar;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GrammarBuilder {
    public static Option option(String ruleName, boolean output, Iterable<Item> items) {
        return new Option(ruleName, output, items);
    }

    public static Option option(String ruleName, Iterable<Item> items) {
        return option(ruleName, false, items);
    }

    public static Option option(String ruleName, boolean output) {
        return option(ruleName, output, emptyList());
    }

    public static Option option(String ruleName) {
        return option(ruleName, false);
    }

    public static RuleItem rule(String ruleName, boolean output) {
        return new RuleItem(ruleName, output);
    }

    public static RuleItem rule(String ruleName) {
        return rule(ruleName, false);
    }

    public static TokenItem token(int elevation, String tokenType, boolean outputType, String tokenValue, boolean outputValue) {
        return new TokenItem(elevation, tokenType, outputType, tokenValue, outputValue);
    }

    public static TokenItem token(int elevation, String tokenType, boolean outputType, boolean outputValue) {
        return token(elevation, tokenType, outputType, null, outputValue);
    }

    public static TokenItem token(int elevation, String tokenType) {
        return token(elevation, tokenType, false, false);
    }

    public static TokenItem token(int elevation) {
        return token(elevation, null, false, false);
    }

    public static TokenItem token(String tokenType, boolean outputType, String tokenValue, boolean outputValue) {
        return token(0, tokenType, outputType, tokenValue, outputValue);
    }

    public static TokenItem token(String tokenType, boolean outputType, boolean outputValue) {
        return token(tokenType, outputType, null, outputValue);
    }

    public static TokenItem token(String tokenType) {
        return token(tokenType, false, false);
    }

    public static TokenItem token() {
        return token(null, false, false);
    }

    public static Iterable<Item> items(Item ...items) {
        return List.of(items);
    }

    public static Iterable<Option> options(Option ...options) {
        return List.of(options);
    }

    public static Grammar grammar(Iterable<Option> options) {
        return new Grammar(options);
    }
}
