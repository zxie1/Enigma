package enigma;

import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author Yuan Xie.
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _myRotors = new Rotor[_numRotors];
        _allRotors = allRotors.toArray();
        _plugboardPerm = new Permutation("", _alphabet);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return my rotors represented as an array. */
    Rotor[] myRotors() {
        return _myRotors;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int r = 0; r < rotors.length; r += 1) {
            for (int s = 0; s < _allRotors.length; s += 1) {
                if (((Rotor) _allRotors[s]).name().equals(rotors[r])) {
                    _myRotors[r] = (Rotor) _allRotors[s];
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int r = 0; r < setting.length(); r += 1) {
            _myRotors[r + 1].set(setting.charAt(r));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboardPerm = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        Boolean[] advance = new Boolean[_myRotors.length];
        for (int r = 0; r < advance.length; r += 1) {
            advance[r] = false;
        }
        for (int r = _myRotors.length - numPawls() + 1;
             r < _myRotors.length; r += 1) {
            if (_myRotors[r].atNotch()) {
                advance[r] = true;
                advance[r - 1] = true;
            }
        }
        advance[_myRotors.length - 1] = true;
        for (int r = 0; r < _myRotors.length; r += 1) {
            if (advance[r]) {
                _myRotors[r].advance();
            }
        }
        int result = _plugboardPerm.permute(c);
        for (int r = _myRotors.length - 1; r >= 0; r -= 1) {
            result = _myRotors[r].convertForward(result);
        }

        for (int r = 1; r < _myRotors.length; r += 1) {
            result = _myRotors[r].convertBackward(result);
        }
        return _plugboardPerm.invert(result);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.toUpperCase();
        String result = "";
        for (int r = 0; r < msg.length(); r += 1) {
            result += Character.toString(_alphabet.toChar(convert(
                    _alphabet.toInt(msg.charAt(r)))));
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors I have. */
    private int _numRotors;

    /** Number of pawls I have. */
    private int _pawls;

    /** Collection of my rotors represented as an Array. */
    private Rotor[] _myRotors;

    /** Collection of all available rotors represented as an Array. Used arrays
     *  because of the convenient toArray method for collections and because
     *  they are ordered. */
    private Object[] _allRotors;

    /** The plugboard for this machine. */
    private Permutation _plugboardPerm;

}
