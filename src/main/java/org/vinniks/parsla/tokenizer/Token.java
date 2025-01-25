package org.vinniks.parsla.tokenizer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Token {
    @NonNull
    String type;

    String value;

    public Token(String type) {
        this(type, null);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder().append(type);

        if (value != null) {
            builder.append(" \"").append(value).append('"');
        }

        return builder.toString();
    }
}
