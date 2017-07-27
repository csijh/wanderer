package view;
import model.*;
import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.event.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

Interpret the program's command line arguments, handle recording and playback of
levels, deal with animation ticks, and provide a button bar for the user
interface. The command line arguments accepted are:

    java ... [level] [-r out] [-p in] [-s steps]

The level argument lets you start the game on a given level.
The -r argument allows the play to be recorded in a given output file.
The -p argument allows the game to be replayed from a given input file.
A recording or playback name is a level name with a one-letter suffix.
If two or more of these three options are given, the level name must agree.
The -s argument specifies how many steps to replay from the file.
After replaying a given number of steps, you can continue playing manually.
You can replay and record at the same time. */

public class Controller extends Scene {
    private Level<?> level;
    private Stage stage;
    private Display display;
    private Table table;
    private Help help;
    private Ticker ticker;
    private String[] levels;
    private String[] names;
    private int index;

    private int number;
    private String prefix;
    private Scanner in;
    private PrintWriter out;
    private int steps;

    // Create a controller from the name of the game, the main stage, a level
    // object, and the list of level files.
    public Controller(String game, Stage s, Level<?> l, String[] ls) {
        super(new BorderPane());
        s.setTitle(game);
        stage = s;
        level = l;
        levels = ls;
        names = new String[levels.length];
        for (int i=0; i<levels.length; i++) names[i] = name(levels[i], false);
        display = new Display(level, 42*24, 18*24);
        if (s != null) table = new Table(game, names);
        if (s != null) help = new Help(s, 700, 500);
        ticker = new Ticker(level, display, table);
        BorderPane pane = (BorderPane) getRoot();
        pane.setTop(createButtons());
        pane.setCenter(display);
    }

    // Set up the controller from the program's command line arguments. Call
    // after setting the scene, but before showing the stage.
    public void setup(List<String> args) {
        stage.getScene().setOnKeyPressed(this::key);
        stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::filter);
        String message = trySetup(args);
        if (message == null) return;
        System.err.println("Error: " + message + ".  Use:");
        System.err.println("  java ... [level] [-r out] [-p in] [-s steps]");
        System.exit(1);
    }

    // Replay by routing commands to the ticker.  Call after showing stage.
    public void replay() {
        if (replaying()) ticker.pause();
        while (replaying()) {
            char cmd = command();
            ticker.command(cmd);
        }
    }

    // Close the program and save the high scores.
    public void close() {
        if (out != null) out.close();
        table.close();
    }

    private HBox createButtons() {
        Button quitButton = new Button("Quit");
        Button restartButton = new Button("Restart");
        Button prevButton = new Button("Prev");
        Button nextButton = new Button("Next");
        Button fasterButton = new Button("Faster");
        Button slowerButton = new Button("Slower");
        Button scoresButton = new Button("Scores");
        Button helpButton = new Button("Help");
        quitButton.setOnAction(this::quit);
        restartButton.setOnAction(this::restart);
        prevButton.setOnAction(this::prev);
        nextButton.setOnAction(this::next);
        fasterButton.setOnAction(ticker::faster);
        slowerButton.setOnAction(ticker::slower);
        scoresButton.setOnAction(table::show);
        helpButton.setOnAction(help::show);
        return new HBox(
            quitButton, restartButton, prevButton, nextButton, fasterButton,
            slowerButton, scoresButton, helpButton
        );
    }

    // Set up controller, returning null or an error message for testing.
    private String trySetup(List<String> args) {
        steps = -1;
        boolean rFlag = false, pFlag = false, sFlag = false;
        String rName = null, pName = null, lName = null;
        try {
            for (String arg : args) {
                if (arg.equals("-r")) rFlag = true;
                else if (arg.equals("-p")) pFlag = true;
                else if (arg.equals("-s")) sFlag = true;
                else if (arg.startsWith("-")) fail("unrecognised option");
                else if (rFlag) { rFlag = false; rName = setupRecording(arg); }
                else if (pFlag) { pFlag = false; pName = setupPlayback(arg); }
                else if (sFlag) { sFlag = false; setupSteps(arg); }
                else lName = setupLevel(arg);
            }
            if (rFlag || pFlag || sFlag) fail("filename required.");
            String name = merge(rName, pName, lName);
            if (name == null) name = table.current();
            if (name == null) name = names[0];
            index = index(name);
            restart(null);
        } catch (Exception e) { return e.getMessage(); }
        return null;
    }

    // Merge the three potential level names
    private String merge(String x, String y, String z) throws Exception {
        String s = x;
        if (s == null) s = y;
        if (s == null) s = z;
        if (y != null && ! y.equals(s) ||
            z != null && ! z.equals(s)) fail("inconsistent levels");
        return s;
    }

    // Get rid of the default behaviour of arrow and space keys.
    private void filter(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) return;
        if (event.getCode() == KeyCode.ENTER) return;
        key(event);
        event.consume();
    }

    // Respond to a key press, including arrow keys.
    private void key(KeyEvent event) {
        switch (event.getCode()) {
            case UP: case W: case K: ticker.command('^'); break;
            case DOWN: case J: ticker.command('v'); break;
            case LEFT: case A: ticker.command('<'); break;
            case RIGHT: case D: case L: ticker.command('>'); break;
            case S:
                if (event.isControlDown()) table.show(null);
                else ticker.command('v');
                break;
            case H:
                if (event.isControlDown()) help.show(null);
                else ticker.command('<');
                break;
            case SPACE: ticker.command('.'); break;
            case R: if (event.isControlDown()) restart(null); break;
            case P: if (event.isControlDown()) prev(null); break;
            case N: if (event.isControlDown()) next(null); break;
            case PLUS: if (event.isControlDown()) ticker.faster(null); break;
            case MINUS: if (event.isControlDown()) ticker.slower(null); break;
        }
    }

    // Respond to the quit button.
    private void quit(ActionEvent e) { Platform.exit(); }

    // Start or restart a level.
    private void restart(ActionEvent e) {
        level.load(levels[index]);
        table.current(names[index]);
        display.setup();
    }

    // Move to the next level.
    private void next(ActionEvent e) {
        if (index < names.length - 1) index++;
        restart(null);
    }

    // Move to the previous level.
    private void prev(ActionEvent e) {
        if (index > 0) index--;
        restart(null);
    }

    // Find out if there is anything more to replay
    private boolean replaying() {
        if (in == null) return false;
        if (! in.hasNextLine() || steps == 0) {
            in.close();
            in = null;
            return false;
        }
        return true;
    }

    // Get the next command from the replay file.  Return '\0' if there isn't
    // one, or the file ends, or the given number of steps is reached.
    private char command() {
        if (in == null) throw new Error("No replay file");
        if (! in.hasNextLine() || steps <= 0) {
            throw new Error("No more replay steps");
        }
        steps--;
        return in.nextLine().charAt(0);
    }

    private String setupRecording(String arg) throws Exception {
        out = new PrintWriter(new File(arg));
        level.record(out);
        return name(arg, true);
    }

    private String setupPlayback(String arg) throws Exception {
        File file = new File(arg);
        if (! file.canRead()) fail("can't open file " + arg);
        in = new Scanner(file);
        return name(arg, true);
    }

    private void setupSteps(String s) throws Exception {
        try { steps = Integer.parseInt(s); }
        catch (Exception e) { fail("# steps not integer"); }
    }

    private String setupLevel(String arg) throws Exception {
        return name(arg, false);
    }

    // Get an index number from a level name.
    private int index(String name) throws Exception {
        for (int i=0; i<levels.length; i++) {
            if (name.equals(names[i])) return i;
        }
        fail("unknown level");
        return -1;
    }

    // Get a level name from a file path for a level or test.
    private String name(String path, boolean test) {
        int slash = path.lastIndexOf('/');
        if (slash >= 0) path = path.substring(slash + 1);
        if (path.endsWith(".txt")) path = path.substring(0, path.length() - 4);
        if (test) path = path.substring(0, path.length() - 1);
        return path;
    }

    // Throw an exception with a given message.
    private void fail(String s) throws Exception { throw new Exception(s); }

    // No Testing
    public static void main(String[] args) {
        System.out.println("Controller class OK");
    }
}
