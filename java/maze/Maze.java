package maze;
import model.*;
import view.*;
import javafx.application.*;
import javafx.stage.*;

/* Maze game. Free and open source: see licence.txt.

This is a cut-down version of the wanderer game, to illustrate simple use of the
wanderer framework. Collect all the stars to win the level. (Note the help pages
are wanderer's.) */

public class Maze extends Application implements Game<Item> {
    private Controller<Item> controller;

    public void start(Stage stage) {
        controller = new Controller<>(stage, this, getParameters().getRaw());
    }

    // Tidy up when the application closes (quit button or window close).
    public void stop() { controller.close(); }

    // Implement the Game interface.
    public String name() { return "Maze"; }
    public int levelCount() { return 1; }
    public String levelPath(int i) { return "/levels/maze.txt"; }
    public Item hatch(char type) { return new Item(); }
    public String imagePath(char type) { return Item.image(type); }
    public Cell<Item> player(Level<Item> level) { return Item.player(level); }
    public String status(Level<Item> level) { return Item.status(level); }
    public int score(Level<Item> level) { return level.count(Item.STARS); }
    public boolean success(Level<Item> level) { return score(level) == 0; }
}
