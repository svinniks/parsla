package org.vinniks.parsla.grammar;

import lombok.NonNull;
import lombok.Value;

@Value
public class RuleItem implements Item {
    @NonNull
    String ruleName;

    boolean output;
}
