package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Yuan Xie.
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();
        String nextLine = _input.nextLine();
        if (!nextLine.contains("*")) {
            throw EnigmaException.error("First line must be a settings line.");
        }
        while (_input.hasNextLine()) {
            String setting = nextLine.toUpperCase();
            setUp(M, setting);
            int i = 0;
            int j = 0;
            Rotor[] a = M.myRotors();
            for (int r = 0; r < a.length; r += 1) {
                if (a[r].rotates()) {
                    i += 1;
                } else if (a[r].reflecting()) {
                    j += 1;
                }
            }
            if (i != M.numPawls()) {
                throw EnigmaException.error("Wrong number of moving rotors.");
            }
            if (j > 1) {
                throw EnigmaException.error("Can only have one reflector.");
            }
            nextLine = _input.nextLine().toUpperCase();
            while (!nextLine.contains("*")) {
                nextLine = nextLine.replace(" ", "");
                String result = M.convert(nextLine);
                printMessageLine(result);
                if (nextLine.equals("")) {
                    _output.println();
                }
                if (_input.hasNextLine()) {
                    nextLine = _input.nextLine().toUpperCase();
                } else {
                    break;
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String characterRange = _config.nextLine().toUpperCase();
            if (characterRange.equals("ABCDEFGHIJKLMNOPQRSTUVWXYZ")) {
                _alphabet = new CharacterRange('A', 'Z');

            } else if (characterRange.contains(("-"))) {
                char first = characterRange.charAt(0);
                char last = characterRange.charAt(2);
                _alphabet = new CharacterRange(first, last);
            } else {
                _alphabet = new GeneralAlphabet(characterRange);
            }
            if (!_config.hasNext()) {
                throw EnigmaException.error("Number of rotors not specified.");
            }
            int numRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw EnigmaException.error("Number of pawls not specified.");
            }
            int numPawls = _config.nextInt();
            rotorName = _config.next().toUpperCase();
            rotors = new ArrayList<>(1);
            while (_config.hasNext()) {
                rotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = rotorName;
            String typeNotch = _config.next();
            String type = Character.toString(typeNotch.charAt(0));
            String next = _config.next();
            String cycles = "";
            while (next.contains("(") && _config.hasNext()) {
                if (next.contains(" ")) {
                    throw EnigmaException.error("Space inside cycle.");
                }
                if (!next.contains(")")) {
                    throw EnigmaException.error("Missing ).");
                }
                next = next.toUpperCase();
                cycles += next;
                next = _config.next();
            }
            if (!_config.hasNext()) {
                cycles += next.toUpperCase();
            }
            rotorName = next.toUpperCase();
            Permutation permutation = new Permutation(cycles,  _alphabet);
            if (type.equals("R")) {
                if (!permutation.derangement()) {
                    throw EnigmaException.error("Reflector's permutation "
                            + "must be a derangement.");
                }
                return new Reflector(name, permutation);
            } else if (type.equals("N")) {
                return new FixedRotor(name, permutation);
            } else {
                return new MovingRotor(name, permutation,
                        typeNotch.substring(1));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] settingsArray = settings.split(" ");
        if (!settingsArray[0].equals("*")) {
            throw EnigmaException.error("Wrong specification "
                    + "format for settings.");
        }
        int i = 1;
        for (int r = 1; r < settingsArray.length; r += 1) {
            for (int s = 0; s < rotors.size(); s += 1) {
                String name = rotors.get(s).name();
                if (name.equals(settingsArray[r])) {
                    i += 1;
                }
            }
        }
        String setting = settingsArray[i].toUpperCase();
        if (setting.length() < M.numRotors() - 1) {
            throw EnigmaException.error("Must specify setting for all rotors "
                    + "except for reflector or rotor does not exist.");
        }
        if (i - 1 != M.numRotors()) {
            throw EnigmaException.error("Incorrect number of rotors.");
        }
        String[] myRotors = new String[M.numRotors()];
        for (int r = 1; r < i; r += 1) {
            int a = 0;
            while (a < r - 1) {
                if (myRotors[a].equals(settingsArray[r])) {
                    throw EnigmaException.error("Rotor is repeated.");
                }
                a += 1;
            }
            myRotors[r - 1] = settingsArray[r];
        }
        String plugboardCycle = "";
        for (int r = i + 1; r < settingsArray.length; r += 1) {
            if (!settingsArray[r].contains("(")
                    || !settingsArray[r].contains(")")) {
                throw EnigmaException.error("Plugboard cycles should "
                        + "be of the form (XY).");
            }
            plugboardCycle += " " + settingsArray[r];
        }
        M.insertRotors(myRotors);
        if (!M.myRotors()[0].reflecting()) {
            throw EnigmaException.error("First rotor of machine must "
                    + "be a reflector.");
        }
        for (int r = 0; r < setting.length(); r += 1) {
            if (!_alphabet.contains(setting.charAt(r))) {
                throw EnigmaException.error("Setting out of alphabet range.");
            }
        }
        M.setRotors(setting);
        M.setPlugboard(new Permutation(plugboardCycle, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int i = 0;
        String s = "";
        for (int r = 0; r < msg.length(); r += 1) {
            i += 1;
            s += Character.toString(msg.charAt(r));
            if (r == msg.length() - 1) {
                _output.println(s);
            } else if (i == 5) {
                _output.print(s + " ");
                i = 0;
                s = "";
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Name of rotor that I am currently in the process of adding. */
    private String rotorName;

    /** ArrayList of all available rotors. */
    private ArrayList<Rotor> rotors;
}
