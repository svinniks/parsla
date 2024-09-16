package org.vinniks.parsla.tokenizer.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TextPosition {
    private final int line;
    private final int column;

    @Override
    public String toString() {
        return line + ":" + column;
    }
}
