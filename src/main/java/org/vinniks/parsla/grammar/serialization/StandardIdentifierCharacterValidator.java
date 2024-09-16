package org.vinniks.parsla.grammar.serialization;

public class StandardIdentifierCharacterValidator implements IdentifierCharacterValidator {
    @Override
    public boolean isValidFirstCharacter(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }

    @Override
    public boolean isValidNextCharacter(char c) {
        return c == '-' || c == '_' || c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }
}
