package wanderer;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A Balloon rises, sliding left or right if there is a deflector or another
balloon above.  It is relatively harmless and doesn't kill, but can be popped by
an arrow. */

class Balloon extends Entity {
    public char code() { return '^'; }

    public void hatch() { background(Space); }

    public void act() { meet(find(Up)); }

    void meetSpace(Entity e) { go(Up); }

    void meetBoulder(Entity e) {
        Entity left = find(Left), upLeft = find(UpLeft);
        Entity right = find(Right), upRight = find(UpRight);
        if (left.is(Space) && upLeft.is(Space)) go(UpLeft);
        else if (right.is(Space) && upRight.is(Space)) go(UpRight);
    }

    void meetRightDeflector(Entity e) {
        Entity left = find(Left), upLeft = find(UpLeft);
        if (left.is(Space) && upLeft.is(Space)) go(UpLeft);
    }

    void meetLeftDeflector(Entity e) {
        Entity right = find(Right), upRight = find(UpRight);
        if (right.is(Space) && upRight.is(Space)) go(UpRight);
    }

    void go(Direction d) {
        advance(Up);
        move(d);
        going(Up);
    }
}
