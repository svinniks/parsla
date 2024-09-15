package org.vinniks.parsla.grammar.serialization;

import org.vinniks.parsla.grammar.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

public class StandardGrammarWriter implements GrammarWriter {
    @Override
    public void write(Grammar grammar, Writer writer) throws IOException {
        String ruleName = null;

        for (var option : grammar.getOptions()) {
            if (!option.getRuleName().equals(ruleName)) {
                writer.append('\n');
            }

            writeOption(option, writer);
            ruleName = option.getRuleName();
        }
    }

    private void writeOption(Option option, Writer writer) throws IOException {
        writer
            .append(option.isOutput() ? ">" : "")
            .append(option.getRuleName())
            .append(":");

        var empty = true;

        for (var item : option.getItems()) {
            writer.append(' ');
            writeItem(item, writer);
            empty = false;
        }

        if (empty) {
            writer.append(" ^");
        }

        writer.append(";\n");
    }

    private void writeItem(Item item, Writer writer) throws IOException {
        if (item instanceof RuleItem ruleItem) {
            writer
                .append(ruleItem.isOutput() ? ">" : "")
                .append(ruleItem.getRuleName());
        } else if (item instanceof TokenItem tokenItem) {
            writer
                .append('{')
                .append(tokenItem.isOutputType() ? ">" : "")
                .append(Optional.ofNullable(tokenItem.getTokenType()).orElse(""));

            if (tokenItem.getTokenValue() != null || tokenItem.isOutputValue()) {
                writer
                    .append(", ")
                    .append(tokenItem.isOutputValue() ? ">" : "")
                    .append(Optional
                        .ofNullable(tokenItem.getTokenValue())
                        .map(value -> '"' + value.replace("\"", "\\\"") + '"')
                        .orElse("")
                    );
            }

            writer.append('}');

            for (var i = 1; i <= tokenItem.getElevation(); i++) {
                writer.append('!');
            }
        }
    }
}
