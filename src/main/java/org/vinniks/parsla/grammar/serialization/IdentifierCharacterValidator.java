package org.vinniks.parsla.grammar.serialization;

public interface IdentifierCharacterValidator {
    boolean isValidFirstCharacter(char c);
    boolean isValidNextCharacter(char c);
}
