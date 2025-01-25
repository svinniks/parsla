package org.vinniks.parsla.grammar;

import lombok.Value;

@Value
public class TokenItem implements Item {
    int elevation;
    String tokenType;
    boolean outputType;
    String tokenValue;
    boolean outputValue;
}
