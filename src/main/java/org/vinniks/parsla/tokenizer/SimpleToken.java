package org.vinniks.parsla.tokenizer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SimpleToken implements Token {
    @NonNull
    private final String type;

    private final String value;

    public SimpleToken(String type) {
        this(type, null);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder().append('{').append(type);

        if (value != null) {
            builder.append(", \"").append(value).append('"');
        }

        return builder.append('}').toString();
    }
}
