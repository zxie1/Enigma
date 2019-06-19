package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Yuan Xie.
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkSize() {
        perm = new Permutation("", UPPER);
        assertEquals(26, perm.size());
        perm = new Permutation("", new CharacterRange('A', 'D'));
        assertEquals(4, perm.size());
    }

    @Test
    public void checkPermute() {
        perm = new Permutation(NAVALA.get("I"), UPPER);
        assertEquals(4, perm.permute(0));
        perm = new Permutation(NAVALA.get("V"), UPPER);
        assertEquals(1, perm.permute(2));
    }

    @Test
    public void checkInvert() {
        perm = new Permutation(NAVALA.get("I"), UPPER);
        assertEquals(0, perm.invert(4));
        perm = new Permutation(NAVALA.get("II"), UPPER);
        assertEquals(18, perm.invert(25));

    }

    @Test
    public void checkPermuteChar() {
        perm = new Permutation(NAVALA.get("I"), UPPER);
        assertEquals('E', perm.permute('A'));
        perm = new Permutation(NAVALB.get("I"), UPPER);
        assertEquals('G', perm.permute('O'));
    }

    @Test
    public void checkInvertChar() {
        perm = new Permutation(NAVALA.get("I"), UPPER);
        assertEquals('A', perm.invert('E'));
        perm = new Permutation(NAVALB.get("I"), UPPER);
        assertEquals('Z', perm.invert('D'));
    }

    @Test
    public void checkDerangement() {
        perm = new Permutation(NAVALA.get("I"), UPPER);
        assertEquals(false, perm.derangement());
        perm = new Permutation(NAVALA.get("B"), UPPER);
        assertEquals(true, perm.derangement());
    }

}
