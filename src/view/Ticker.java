package view;
import model.*;
import javafx.animation.*;
import javafx.event.*;
import javafx.util.*;
import java.util.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This is an animation timer, controlling the level and calling a provided method
whenever redrawing is needed.  User commands are given to the timer, queued up
for later execution if an animation is in progress, and passed on to the level.
The provided method is called with a null ActionEvent argument.

Animations only occur just after the user has pressed a key.  And since entities
move one cell at a time rather than smoothly, animation steps need not be very
frequent.  So to avoid unnecessary processing (especially on mobile devices)
fast animation, e.g. with AnimationTimer, is not used. Instead, PauseTransition
is used to sleep for one tick when needed.

Since pauses can be quite long, an extra unnecessary pause may be perceptible to
the user.  So, first level.step() is called, then the boolean result is checked
to see if anything actually happened.  If the result is false, there is no
pause.  If the result is true, a pause triggered, followed by a call to the
provided method to make the change visible. */

class Ticker {
    private Level level;
    private Display display;
    private Table table;
    private int speed = 5;
    private PauseTransition pause;
    private Deque<Character> commands;
    private boolean animating;

    // The speeds go from 0 to 10, halving the pause each time, with default 5
    // (32 milliseconds pause). Pauses below 16 milliseconds involve multiple
    // steps per frame, but are useful to skip quickly through replays.
    private static double[] speeds = {1024,512,256,128,64,32,16,8,4,2,1};

    // Create a new timer, with a given level, display, and table.
    public Ticker(Level l, Display d, Table t) {
        level = l;
        display = d;
        table = t;
        Duration time = Duration.seconds(speeds[speed] / 1000.0);
        pause = new PauseTransition(time);
        pause.setOnFinished(this::tick);
        commands = new ArrayDeque<Character>();
    }

    // Pause for a second, e.g. before starting a replay.
    public void pause() {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(this::endPause);
        animating = true;
        pause.play();
    }

    private void endPause(ActionEvent e) {
        animating = false;
        backlog();
    }

    public void faster(ActionEvent e) {
        speed++;
        if (speed > 10) speed = 10;
        pause.setDuration(Duration.seconds(speeds[speed] / 1000.0));
    }

    public void slower(ActionEvent e) {
        speed--;
        if (speed < 0) speed = 0;
        pause.setDuration(Duration.seconds(speeds[speed] / 1000.0));
    }

    // Accept a user command.
    public void command(char cmd) {
        commands.addLast(cmd);
        backlog();
    }

    // Check for queued commands.  For each, make the player's move, then see if
    // there is a subsequent animation needing pauses.
    void backlog() {
        if (animating) return;
        boolean b = false;
        while (! b) {
            Character cmd = commands.pollFirst();
            if (cmd == null) return;
            level.command(cmd);
            b = level.step();
            display.redraw();
        }
        animating = true;
        pause.play();
    }

    // Called at the end of a pause.  Draw the changes from the previous step,
    // then take the next step and see if the animation has finished.
    void tick(ActionEvent e) {
        display.redraw();
        String name = level.name();
        table.current(name);
        table.score(name, level.score());
        table.success(name, level.success());
        boolean b = level.step();
        if (b) pause.play();
        else {
            animating = false;
            backlog();
        }
    }

    // No testing
    public static void main(String[] args) {
        System.out.println("Ticker class OK");
    }
}
