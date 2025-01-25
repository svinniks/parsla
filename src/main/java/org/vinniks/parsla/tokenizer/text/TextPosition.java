package org.vinniks.parsla.tokenizer.text;

import lombok.Value;

@Value
public class TextPosition {
    int line;
    int column;

    @Override
    public String toString() {
        return line + ":" + column;
    }
}
