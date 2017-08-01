package wanderer;
import model.*;
import view.*;
import javafx.application.*;
import javafx.stage.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

Play the 1980s retro game Wanderer. The aim on each level is to collect all the
stars and then find the exit. For command line options, see the Controller
class. */

public class Wanderer extends Application {
    private Level<Entity> level;
    private Controller<Entity> controller;

    // Make sure that compiling Wanderer causes Test to be compiled.
    private Test test;

    public void start(Stage stage) {
        level = new Level<>(Test.samples);
        for (Entity e : Test.samples) e.init(level);
        controller = new Controller<>("Wanderer", stage, level, levels());
        stage.setScene(controller);
        controller.setup(getParameters().getRaw());
        stage.show();
        controller.replay();
    }

    // Generate the list of level files.
    String[] levels() {
        String[] list = new String[60];
        for (int i=0; i<60; i++) list[i] = "/levels/" + (i+1) + ".txt";
        return list;
    }

    // Tidy up when the application closes (quit button or window close).
    public void stop() { controller.close(); }
}
