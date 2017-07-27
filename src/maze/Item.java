package maze;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This class represents any item in the maze game. The different types of item are
distinguished by a character code.

The Player item represents the user.  Space items are blank spaces where the
player can go.  Wall items provide impenetrable barriers, and in particular
surround a level. Star items are collected by the player to solve the level. */

class Item extends Cell<Item> {
    private char type;

    static Variable STARS = new Variable("STARS");
    static final char Player = '@', Space = '.', Wall = '#', Star = '*';
    static final Item[] samples = {
        new Item(Player), new Item(Space), new Item(Wall), new Item(Star)
    };

    Item(char t) { type = t; }

    // Override the required Cell methods.

    public Item spawn() { return new Item(type); }

    public char code() { return type; }

    public String image() {
        switch (type) {
            case Player: return "images/Player.png";
            case Space: return "images/Space.png";
            case Wall: return "images/Wall.png";
            case Star: return "images/Star.png";
            default: throw new Error("Unknown type");
        }
    }

    public String status() {
        int n = count(STARS);
        if (n > 0) return "Stars: " + n;
        else return "Success!";
    }

    public void hatch() {
        if (type == Star) add(STARS, 1);
        else if (type == Player) {
            set(PLAYER, this);
            agent(true);
        }
    }

    public void act() {
        if (type != Player) return;
        Direction go = Direction.go(command());
        Item it = find(go);
        if (it.type != Space && it.type != Star) return;
        swap(it);
        if (it.type == Star) {
            it.type = Space;
            subtract(STARS, 1);
        }
    }

    public static void main(String[] args) {
        System.out.println("Item class OK");
    }
}
