package org.vinniks.parsla.tokenizer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TextPosition {
    private final int line;
    private final int column;
    private final int length;

    @Override
    public String toString() {
        return "(" + line + ", " + column + ")";
    }
}
