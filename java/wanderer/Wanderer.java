package wanderer;
import model.*;
import static wanderer.Entity.*;
import view.*;
import javafx.application.*;
import javafx.stage.*;

/* Wanderer class. Free and open source: see licence.txt.

Play the 1980s retro game Wanderer. The aim on each level is to collect all the
stars and then find the exit. For command line options, see the Controller
class. */

public class Wanderer extends Application implements Game<Entity> {
    private Controller<Entity> controller;
    private Test test = new Test();

    public void start(Stage stage) {
        controller = new Controller<>(stage, this, getParameters().getRaw());
    }

    // Tidy up when the application closes (quit button or window close).
    public void stop() { controller.close(); }

    // Implement the Game interface.
    public String name() { return "Wanderer"; }
    public int levelCount() { return 60; }
    public String levelPath(int i) { return "/levels/" + (i+1) + ".txt"; }
    public Entity hatch(char type) { return test.hatch(type); }

    public String imagePath(char type) {
        switch (type) {
            case Space: return "/images/Space.png";
            case Wall: return "/images/Wall.png";
            case Rock: return "/images/Rock.png";
            case Earth: return "/images/Earth.png";
            case Star: return "/images/Star.png";
            case Cage: return "/images/Cage.png";
            case Time: return "/images/Time.png";
            case Landmine: return "/images/Landmine.png";
            case Arrival: return "/images/Arrival.png";
            case Teleport: return "/images/Teleport.png";
            case Exit: return "/images/Exit.png";
            case LeftDeflector: return "/images/LeftDeflector.png";
            case RightDeflector: return "/images/RightDeflector.png";
            case Dead: return "/images/Dead.png";
            case Boulder: return "/images/Boulder.png";
            case Balloon: return "/images/Balloon.png";
            case LeftArrow: return "/images/LeftArrow.png";
            case RightArrow: return "/images/RightArrow.png";
            case Monster: return "/images/Monster.png";
            case Baby: return "/images/Baby.png";
            case Player: return "/images/Player.png";
            default: return null;
        }
    }

    public Cell<Entity> player(Level<Entity> level) {
        return level.entity("PLAYER");
    }

    public String status(Level<Entity> level) {
        String lev = "Level " + level.name();
        lev += ": " + level.title();
        String message = level.string("MESSAGE");
        if (message.length() == 0) message = "Stars: " + level.count("STARS");
        String score = "Score: " + level.count(SCORE);
        String moves = "Moves: " + level.count(MOVES);
        String gap = "     ";
        String status = lev + gap + message + gap + score + gap + moves;
        return status;
    }

    public int score(Level<Entity> level) {
        return level.count(SCORE);
    }

    public boolean success(Level<Entity> level) {
        return level.count(SUCCESS) > 0;
    }
}
