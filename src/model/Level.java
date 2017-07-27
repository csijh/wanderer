package model;
import java.io.*;
import java.util.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A level object reads in a level file, and drives the mechanics of the game. A
level object can be reused by calling the load method repeatedly. The command
method starts off a command, and the step method carries out an amount of
processing suitable for one tick of an animation.  The command ends when step
returns false.  The play can be recorded in a file by calling the record method
at the start of the level, and the stop method at the end.  The tests method can
be called on a game-specific level object, to carry out comprehensive
replay-based testing from recordings. */

public class Level<E extends Cell<E>> {
    private int width, height, limit;
    private String name, title;
    private char cells[][];
    private StringBuilder changes;
    private PrintWriter out;
    private Grid<E> grid;
    private State<E> state;
    private Queue<E> queue;
    private E[] samples;
    private Map<Character,E> types;

    // Create a level object, passing in a sample of each type of entity.
    public Level(E[] es) {
        samples = es;
        types = new HashMap<>();
        for (E e : es) types.put(e.code(), e);
    }

    // Return the entity samples, and the size after loading.
    public E[] samples() { return samples; }
    public int width() { return width; }
    public int height() { return height; }

    // Delegate methods to the grid and state objects, for view purposes.
    public E front(int x, int y) { return grid.front(x, y); }
    public E player() { return state.entity(Variable.PLAYER); }
    public String name() { return state.string(Variable.NAME); }
    public int score() { return state.count(Variable.SCORE); }
    public boolean success() { return state.count(Variable.SUCCESS) > 0; }
    public String status() { return player().status(); }

    // Load up a level file. It starts with a line giving the width, height and
    // time limit, then the next line contain the title, then there is a grid of
    // entity character codes in matrix (y,x) order. The new level is not
    // assumed to be the same size as the old one.
    public void load(String path) {
        name = path;
        int n = name.lastIndexOf('/');
        if (n >= 0) name = name.substring(n+1);
        n = name.indexOf('.');
        if (n >= 0) name = name.substring(0, n);
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) throw new Error("Can't open " + path);
        Reader r = new InputStreamReader(is);
        Scanner sc = new Scanner(r);
        String line = sc.nextLine();
        String[] parts = line.split(" ");
        width = Integer.parseInt(parts[0]);
        height = Integer.parseInt(parts[1]);
        limit = Integer.parseInt(parts[2]);
        if (limit == 0) limit = 1000;
        title = sc.nextLine();
        cells = new char[width][height];
        for (int y = 0; y < height; y++) {
            line  = sc.nextLine();
            for (int x = 0; x < width; x++) {
                cells[x][y] = line.charAt(x);
            }
        }
        sc.close();
        changes = new StringBuilder();
        grid = new Grid<E>(width, height);
        state = new State<E>(types);
        queue = new Queue<E>();
        state.set(Variable.NAME, name);
        state.set(Variable.TITLE, title);
        state.set(Variable.MOVES, limit);
        fill();
        hatch();
        copy();
    }

    // Accept a command from the user interface, and pass it on.  Start
    // recording the new command.
    public void command(char cmd) {
        queue.command(cmd);
        changes.setLength(0);
        changes.append(cmd);
    }

    // Take one step, e.g. on an animation tick.  Get entities to take actions
    // until one action causes a change. Return false if the effects of the
    // command have finished.
    public boolean step() {
        boolean changed = false;
        grid.changed(false);
        E e = queue.pull();
        while (! changed && e != null) {
            e.act();
            changed = grid.changed();
            if (! changed) e = queue.pull();
        }
        if (changed) recordChanges();
        if (e == null && out != null) out.println(changes.toString());
        return e != null;
    }

    // Record changes and their effects in the given writer.
    public void record(PrintWriter p) {
        out = p;
    }

    // Spawn an entity.
    private E spawn(char c, int x, int y) {
        E type = types.get(c);
        if (type == null) throw new Error("Unknown code '" + c + "'");
        E e = type.spawn();
        e.init(grid, state, queue, x, y);
        return e;
    }

    // Create all the initial entities and put them in the grid.
    private void fill() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = cells[x][y];
                E e = spawn(c, x, y);
                grid.wake(x, y, e);
            }
        }
    }

    // Hatch all the entities.  (Do this after filling the grid with entities,
    // in case entities need to know about each other when hatching.)
    private void hatch() {
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                grid.front(x,y).hatch();
            }
        }
    }

    // Copy the current visible grid state into the cells array, so it can be
    // used to track changes.  (Do this after hatching the entities, in case
    // they cause immediate changes.)
    private void copy() {
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                Cell<E> e = grid.front(x,y);
                cells[x][y] = e.code();
            }
        }
    }

    // Record the changes caused by the immediately preceding step. Also update
    // the cells array ready to track the next set of changes.  The changes
    // during a single step are regarded as simultaneous and are recorded in a
    // standard (x,y) order.
    private void recordChanges() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = grid.front(x,y).code();
                if (c == cells[x][y]) continue;
                changes.append(" " + x + "," + y + "," + c);
                cells[x][y] = c;
            }
        }
    }

    // Test a level according to a recording file. Each line consists of the
    // direction key the user pressed, followed by the changes caused on screen.
    // Each change is a coordinate pair followed by the character code of the
    // new entity at that position.
    private void test(String file) {
        InputStream is = getClass().getResourceAsStream(file);
        Scanner in = new Scanner(is);
        int lineNumber = 1;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            char key = line.charAt(0);
            command(key);
            for (boolean ok = step(); ok; ok = step()) { }
            String newLine = changes.toString();
            if (newLine.equals(line)) { }
            else {
                throw new Error(
                "Test " + name + " fails on line " + lineNumber + "\n" +
                "Actual:   " + newLine + "\n" +
                "Recorded: " + line);
            }
            lineNumber++;
        }
    }

    // Replay-based testing from recording files.  The first argument is an
    // array of test file paths, and the second is an array of corresponding
    // level file paths. A test file contains a recording of some moves in its
    // level. The moves are replayed, without graphics, and the result compared
    // to the recording. Test a game by creating a game-specific level object,
    // and calling this method on it.
    public int tests(String[] ts, String[] ls) {
        for (int i=0; i<ts.length; i++) {
            load(ls[i]);
            test(ts[i]);
        }
        return ts.length;
    }

    // No testing.  Call the tests method from a game-specific class.
    public static void main(String[] args) {
        System.out.println("Level class OK");
    }
}
