package maze;
import model.*;
import view.*;
import javafx.application.*;
import javafx.stage.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This is a cut-down version of the wanderer game, to illustrate simple use of the
wanderer framework. Collect all the stars to win the level. (Note the help pages
are wanderer's.) */

public class Maze extends Application {
    private Controller<Item> controller;

    public void start(Stage stage) {
        Level<Item> level = new Level<>(Item.samples);
        for (Item x : Item.samples) x.init(level);
        String[] levels = { "/levels/maze.txt" };
        controller = new Controller<Item>("Maze", stage, level, levels);
        stage.setScene(controller);
        controller.setup(getParameters().getRaw());
        stage.show();
        controller.replay();
    }

    // Tidy up when the application closes (quit button or window close).
    public void stop() { controller.close(); }
}
