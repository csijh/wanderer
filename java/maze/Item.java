package maze;
import model.*;

/* Maze entity class. Free and open source: see licence.txt.

This class represents any entity in the maze game. (It is called Item to avoid a
name clash in the Makefile with wanderer.Entity)

The Player entity represents the user.  Spaces are where the player can go.
Walls are impenetrable barriers, in particular surrounding a level. Stars are
collected by the player to solve the level. */

class Item extends Cell<Item> {
    static String STARS = "STARS", PLAYER = "PLAYER";
    static final char Player = '@', Space = '.', Wall = '#', Star = '*';

    // Count stars, remember the player.
    public void wake() {
        if (type() == Star) add(STARS, 1);
        else if (type() == Player) {
            set(PLAYER, this);
            agent(true);
        }
    }

    public void act() {
        if (type() != Player) return;
        Direction go = Direction.go(command());
        Item it = find(go);
        if (it.type() != Space && it.type() != Star) return;
        swap(it);
        if (it.type() == Star) {
            it.mutate(Space);
            subtract(STARS, 1);
        }
    }

    static Item create(char type) {
        switch (type) {
            case Player: case Space: case Wall: case Star: return new Item();
            default: return null;
        }
    }

    static String image(char type) {
        switch (type) {
            case Player: return "images/Player.png";
            case Space: return "images/Space.png";
            case Wall: return "images/Wall.png";
            case Star: return "images/Star.png";
            default: return null;
        }
    }

    static Item player(Level<Item> level) {
        return level.entity(PLAYER);
    }

    static String status(Level l) {
        int n = l.count(STARS);
        if (n > 0) return "Stars: " + n;
        else return "Success!";
    }

    public static void main(String[] args) {
        System.out.println("Item class OK");
    }
}
