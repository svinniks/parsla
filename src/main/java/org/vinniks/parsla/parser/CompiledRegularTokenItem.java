package org.vinniks.parsla.parser;

import lombok.Getter;
import org.vinniks.parsla.tokenizer.Token;

import java.util.Objects;
import java.util.Set;

final class CompiledRegularTokenItem extends AbstractCompiledTokenItem {
    @Getter
    private final String tokenType;

    private final boolean outputType;
    private final String tokenValue;
    private final boolean outputValue;

    CompiledRegularTokenItem(
        int elevation, String tokenType, boolean outputType, String tokenValue, boolean outputValue
    ) {
        super(elevation);
        this.tokenType = tokenType;
        this.outputType = outputType;
        this.tokenValue = tokenValue;
        this.outputValue = outputValue;
    }

    @Override
    protected boolean outputType() {
        return outputType;
    }

    @Override
    protected boolean outputValue() {
        return outputValue;
    }

    @Override
    protected boolean matches(Token token, Set<String> ignoredTokenTypes) {
        return
            tokenType == null && !ignoredTokenTypes.contains(token.getType())
            || Objects.equals(token.getType(), tokenType) && (tokenValue == null || Objects.equals(token.getValue(), tokenValue));
    }
}
