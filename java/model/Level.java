package model;
import java.io.*;
import java.util.*;

/* Level class. Free and open source: see licence.txt.

A level object reads in a level file, and drives the mechanics of the game. A
level object can be reused by calling the load method repeatedly. The command
method starts off a command, and the step method carries out an amount of
processing suitable for one tick of an animation.  The command ends when step
returns false.  The play can be recorded in a file by calling the record method
at the start of the level, and the stop method at the end.  The tests method can
be called on a game-specific level object, to carry out comprehensive
replay-based testing from recordings. */

public class Level<E extends Cell<E>> {
    private Hatchery<E> hatchery;
    private Grid<E> grid;
    private State<E> state;
    private Queue<E> queue;
    private int width, height, limit;
    private String name, title;
    private char cells[][];
    private StringBuilder changes;
    private PrintWriter out;

    // Create a level object, passing in a hatchery for creating entities.
    public Level(Hatchery<E> h) {
        hatchery = h;
        grid = new Grid<E>(2, 2);
        state = new State<E>();
        queue = new Queue<E>();
    }

    // Return the size, name, title and move limit after loading.
    public int width() { return width; }
    public int height() { return height; }
    public String name() { return name; }
    public String title() { return title; }
    public int limit() { return limit; }

    // Delegate methods to the grid and state objects, for viewing.
    public E front(int x, int y) { return grid.front(x, y); }
    public E entity(String id) { return state.entity(id); }
    public String string(String id) { return state.string(id); }
    public int count(String id) { return state.count(id); }

    // Load up a level file. The new level grid is not assumed to be the same
    // size as the old one.  The limit on the number of moves is recorded in the
    // state, so that it can be picked up by entities.
    public void load(String path) {
        name = extractFrom(path);
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) throw new Error("Can't open " + path);
        Reader r = new InputStreamReader(is);
        Scanner sc = new Scanner(r);
        levelData(sc);
        sc.close();
        changes = new StringBuilder();
        grid.reset(width, height);
        state.reset();
        queue.reset();
        state.add("MOVES", limit);
        hatch();
        wake();
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
        grid.changed(false);
        E e = queue.next();
        while (! grid.changed() && e != null) {
            e.act();
            if (! grid.changed()) e = queue.next();
        }
        if (grid.changed()) recordChanges();
        if (e == null && out != null) out.println(changes.toString());
        return e != null;
    }

    // Record changes and their effects in the given writer.
    public void record(PrintWriter p) {
        out = p;
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

    // Extract the level name from the path to the level file.
    private String extractFrom(String path) {
        int start = path.lastIndexOf('/');
        if (start < 0) start = 0;
        else start = start + 1;
        int end = path.lastIndexOf('.');
        if (end < start) end = path.length();
        return path.substring(start, end);
    }

    // Read the level data from a scanner.  The data starts with a line giving
    // the width, height and time limit, then there is a title line, then there
    // is a grid of entity character codes in matrix (y,x) order.
    private void levelData(Scanner sc) {
        String line = sc.nextLine();
        String[] parts = line.split(" ");
        width = Integer.parseInt(parts[0]);
        height = Integer.parseInt(parts[1]);
        limit = Integer.parseInt(parts[2]);
        if (limit == 0) limit = 1000;
        title = sc.nextLine();
        cells = new char[width][height];
        for (int y = 0; y < height; y++) {
            line = sc.nextLine();
            for (int x = 0; x < width; x++) {
                cells[x][y] = line.charAt(x);
            }
        }
    }

    // Create all the initial entities and put them in the grid.
    private void hatch() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char t = cells[x][y];
                E e = hatchery.hatch(t);
                if (e == null) throw new Error("Unknown type '" + t + "'");
                e.init(t, hatchery, state, queue, grid, x, y);
                e.show();
            }
        }
    }

    // Wake all the entities.  (Do this after hatching all the entities, in case
    // entities need to know about each other when waking.)
    private void wake() {
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                grid.front(x,y).wake();
            }
        }
    }

    // Copy the current visible grid state into the cells array, so it can be
    // used to track changes.  (Do this after hatching the entities, in case
    // they cause immediate changes.)
    private void copy() {
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                E e = grid.front(x,y);
                cells[x][y] = e.type();
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
                char t = grid.front(x,y).type();
                if (t == cells[x][y]) continue;
                changes.append(" " + x + "," + y + "," + t);
                cells[x][y] = t;
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

    // No testing.  Call the tests method from a game-specific class.
    public static void main(String[] args) {
        System.out.println("Level class OK");
    }
}
