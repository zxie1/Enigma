package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Yuan Xie.
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        int i = 0;
        for (int r = 0; r < cycles.length(); r += 1) {
            if (Character.toString(cycles.charAt(r)).equals("(")) {
                i += 1;
            }
        }
        _cycles = new String[i];
        int l = 0;
        for (int r = 0; r < _cycles.length; r += 1) {
            String result = "";
            for (; l < cycles.length(); l += 1) {
                if (!Character.toString(cycles.charAt(l)).equals("(")
                        && !Character.toString(cycles.charAt(l)).equals(")")) {
                    result += Character.toString(cycles.charAt(l));
                } else if (Character.toString(cycles.charAt(l)).equals(")")) {
                    _cycles[r] = result.replace(" ", "");
                    l += 1;
                    break;
                }
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        String[] cycles1 = new String[_cycles.length + 1];
        for (int r = 0; r < _cycles.length; r += 1) {
            cycles1[r] = _cycles[r];
        }
        cycles1[_cycles.length] = cycle.toUpperCase();
        _cycles = cycles1;

    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Returns the cycles of this permutation as a string array. */
    void removeFirstCycle() {
        String[] c = new String[_cycles.length - 1];
        for (int r = 1; r < _cycles.length; r += 1) {
            c[r - 1] = _cycles[r];
        }
        _cycles = c;
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _alphabet.toInt(permute(_alphabet.toChar(wrap(p))));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _alphabet.toInt(invert(_alphabet.toChar(wrap(c))));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        for (int r = 0; r < _cycles.length; r += 1) {
            if (_cycles[r].contains(Character.toString(p))) {
                for (int s = 0; s < _cycles[r].length(); s += 1) {
                    if (p == _cycles[r].charAt(s)) {
                        if (s == _cycles[r].length() - 1) {
                            return _cycles[r].charAt(0);
                        } else {
                            return _cycles[r].charAt(s + 1);
                        }
                    }
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C.
     *  Note that I changed int to char. */
    char invert(char c) {
        for (int r = 0; r < _cycles.length; r += 1) {
            if (_cycles[r].contains(Character.toString(c))) {
                for (int s = 0; s < _cycles[r].length(); s += 1) {
                    if (c == _cycles[r].charAt(s)) {
                        if (s == 0) {
                            return _cycles[r].charAt(_cycles[r].length() - 1);
                        } else {
                            return _cycles[r].charAt(s - 1);
                        }
                    }
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int counter = 0;
        for (int r = 0; r < _cycles.length; r += 1) {
            if (_cycles[r].length() == 1) {
                return false;
            } else {
                counter += _cycles[r].length();
            }
        }
        return counter == _alphabet.size();
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private String[] _cycles;
}
