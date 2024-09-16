package org.vinniks.parsla.grammar.serialization;

class ExtendedIdentifierCharacterValidator extends StandardIdentifierCharacterValidator {
    @Override
    public boolean isValidNextCharacter(char c) {
        return super.isValidNextCharacter(c) || c == '#';
    }
}
