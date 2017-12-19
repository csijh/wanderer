package model;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This defines the nine directions you can go in from any position (including
staying put).  Each direction has x and y offsets, and methods for turning left
or right or back. */

public enum Direction {
    Here(0,0), Up(0,-1), Down(0,1), Right(1,0), Left(-1,0),
    UpLeft(-1,-1), UpRight(1,-1), DownLeft(-1,1),DownRight(1,1);

    final int x, y;

    private Direction(int x0, int y0) { x = x0; y = y0; }

    // Turn left, i.e. find the direction to the left of the given direction.
    public Direction left() {
        switch (this) {
            case Here: return Here;
            case Up: return Left;
            case Left: return Down;
            case Down: return Right;
            case Right: return Up;
            case UpLeft: return DownLeft;
            case DownLeft: return DownRight;
            case DownRight: return UpRight;
            case UpRight: return UpLeft;
            default: throw new Error("Nasty turn");
        }
    }

    // Find the reverse direction to the given direction.
    public Direction back() { return left().left(); }

    // Find the direction to the right of the given direction.
    public Direction right() { return left().left().left(); }

    // Convert a command into a direction.
    public static Direction go(char cmd) {
        switch (cmd) {
            case '^': return Up;
            case 'v': return Down;
            case '<': return Left;
            case '>': return Right;
            case '.': return Here;
            default: throw new Error("Bug");
        }
    }

    // Testing.
    private static void claim(boolean b) { if (! b) throw new Error("Bug"); }
    public static void main(String[] args) {
        for (Direction d : values()) {
            Direction bd = d.back();
            claim(bd.back() == d);
            claim(bd.x + d.x == 0);
            claim(bd.y + d.y == 0);
        }
        System.out.println("Direction class OK");
    }
}
