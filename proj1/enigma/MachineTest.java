package enigma;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Yuan Xie.
 */
public class MachineTest {

    private Machine machine;
    private Reflector b =
            new Reflector("B", new Permutation(NAVALA.get("B"), UPPER));
    private FixedRotor beta =
            new FixedRotor("Beta", new Permutation(NAVALA.get("Beta"), UPPER));
    private MovingRotor i =
            new MovingRotor("I", new Permutation(NAVALA.get("I"), UPPER), "Q");
    private MovingRotor ii =
            new MovingRotor("II", new Permutation(NAVALA.get("II"), UPPER),
                    "E");
    private MovingRotor iii =
            new MovingRotor("III", new Permutation(NAVALA.get("III"), UPPER),
                    "V");
    private MovingRotor iv =
            new MovingRotor("IV", new Permutation(NAVALA.get("IV"), UPPER),
                    "J");
    private ArrayList<Rotor> allrotors = new ArrayList<Rotor>(6);

    @Test
    public void checkinsertRotors() {
        allrotors.addAll(List.of(b, beta, i, ii, iii, iv));
        machine = new Machine(UPPER, 5, 3, allrotors);
        String[] myrotors = {"B", "Beta", "III", "IV", "I"};
        machine.insertRotors(myrotors);
        Rotor[] result = {b, beta, iii, iv, i};
        assertArrayEquals(result, machine.myRotors());
    }

    @Test
    public void checksetRotors() {
        allrotors.addAll(List.of(b, beta, i, ii, iii, iv));
        machine = new Machine(UPPER, 5, 3, allrotors);
        String[] myrotors = {"B", "Beta", "III", "IV", "I"};
        machine.insertRotors(myrotors);
        machine.setRotors("AXLZ");
        assertEquals(0, machine.myRotors()[1].setting());
        assertEquals(25, machine.myRotors()[4].setting());
    }

    @Test
    public void checkConvert() {
        allrotors.addAll(List.of(b, beta, i, ii, iii, iv));
        machine = new Machine(UPPER, 5, 3, allrotors);
        String[] myrotors = {"B", "Beta", "III", "IV", "I"};
        machine.insertRotors(myrotors);
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation("(YF) (HZ)", UPPER));
        assertEquals(25, machine.convert(24));
    }

    @Test
    public void checkConvertString() {
        allrotors.addAll(List.of(b, beta, i, ii, iii, iv));
        machine = new Machine(UPPER, 5, 3, allrotors);
        String[] myrotors = {"B", "Beta", "I", "II", "III"};
        machine.insertRotors(myrotors);
        machine.setRotors("AACA");
        assertEquals("GCMSIVDCVKLXT", machine.convert("Enigmamachine"));

        allrotors.addAll(List.of(b, beta, i, ii, iii, iv));
        machine = new Machine(UPPER, 5, 3, allrotors);
        String[] myrotors1 = {"B", "Beta", "I", "II", "III"};
        machine.insertRotors(myrotors1);
        machine.setRotors("AAAA");
        machine.setPlugboard(new Permutation("(AQ) (EP)", UPPER));
        assertEquals("IHBDQQMTQZ", machine.convert("Helloworld"));
    }

}
