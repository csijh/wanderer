package wanderer;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A Balloon rises, sliding left or right if there is a deflector or another
balloon above.  It is relatively harmless and doesn't kill, but can be popped by
an arrow. */

class Balloon extends Entity {
    public char code() { return '^'; }
    void isMetBy(Entity e) { e.meet(this); }

    public void hatch() { background(Space); }

    public void act() { find(Up).isMetBy(this); }

    void meet(Space s) { go(Up); }

    void meet(Boulder b) {
        Entity left = find(Left), upLeft = find(UpLeft);
        Entity right = find(Right), upRight = find(UpRight);
        if (left.is(Space) && upLeft.is(Space)) go(UpLeft);
        else if (right.is(Space) && upRight.is(Space)) go(UpRight);
    }

    void meet(Thing t) {
        if (t.is(RightDeflector)) {
            Entity left = find(Left), upLeft = find(UpLeft);
            if (left.is(Space) && upLeft.is(Space)) go(UpLeft);
        }
        else if (t.is(LeftDeflector)) {
            Entity right = find(Right), upRight = find(UpRight);
            if (right.is(Space) && upRight.is(Space)) go(UpRight);
        }
    }

    void go(Direction d) {
        advance(Up);
        move(d);
        going(Up);
    }
}
