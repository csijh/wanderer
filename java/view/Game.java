package view;
import model.*;

/* Game interface. Free and open source: see licence.txt.

These methods need to be provided to create a custom game. */

public interface Game<E extends Cell<E>> extends Hatchery<E> {
    // The name of the game, used for the title and help page.
    String name();

    // Find the number of levels.
    int levelCount();

    // Find the path of the i'th level file, or null.
    String levelPath(int i);

    // Construct a fresh uninitialised entity of a given type.
    // Satisfies the model.Hatchery interface.
    E hatch(char type);

    // Find the image path for entities of a given type.
    String imagePath(char type);

    // Find the player entity for the current level.
    Cell<E> player(Level<E> level);

    // Find the status message for the current level.
    String status(Level<E> level);

    // Find the current score.
    int score(Level<E> level);

    // At the end of the level, find out whether the player succeeded.
    boolean success(Level<E> level);

    public static void main(String[] args) {
        System.out.println("Game interface OK");
    }
}
