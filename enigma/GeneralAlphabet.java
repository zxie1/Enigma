package enigma;

import static enigma.EnigmaException.*;

/** A General Alphabet.
 *  @author Yuan Xie.
 */
class GeneralAlphabet extends Alphabet {

    /** An general alphabet that takes in ALPHABET. */
    GeneralAlphabet(String alphabet) {
        if (alphabet.contains(")") || alphabet.contains("(")
                || alphabet.contains("-") || alphabet.contains("*")) {
            throw error("empty range of characters");
        } else {
            _string = alphabet;
        }
    }

    @Override
    int size() {
        return _string.length();
    }

    @Override
    boolean contains(char ch) {
        return _string.contains(Character.toString(ch));
    }

    @Override
    char toChar(int index) {
        if (!(index < _string.length())) {
            throw error("character index out of range");
        }
        return _string.charAt(index);
    }

    @Override
    int toInt(char ch) {
        if (!_string.contains(Character.toString(ch))) {
            throw error("character out of range");
        }
        return _string.indexOf(Character.toString(ch));
    }

    /** Range of characters in this Alphabet. */
    private String _string;

}
