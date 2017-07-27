package wanderer;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A Boulder drops, sliding left or right if there is a deflector or another
boulder underneath.  It can kill the player or a monster by falling on it. */

class Boulder extends Entity {
    public char code() { return 'O'; }
    void isMetBy(Entity e) { e.meet(this); }

    private boolean moving;

    public void hatch() {
        background(Space);
        moving = false;
    }

    public void act() {
        Direction go = Here;
        Entity target = find(Down);
        Entity left = find(Left);
        Entity right = find(Right);
        Entity downLeft = find(DownLeft);
        Entity downRight = find(DownRight);
        target.isMetBy(this);
    }

    void meet(Space s) { go(Down); }

    void meet(Monster m) {
        add(SCORE, 100);
        m.sleep();
        m.stop();
        go(Down);
    }

    void meet(Player p) {
        if (! moving) return;
        set(MESSAGE, "Killed by a falling boulder");
        p.die();
    }

    void meet(Boulder b) {
        Entity left = find(Left), downLeft = find(DownLeft);
        Entity right = find(Right), downRight = find(DownRight);
        if (left.is(Space) && downLeft.is(Space)) go(DownLeft);
        else if (right.is(Space) && downRight.is(Space)) go(DownRight);
        else moving = false;
    }

    void meet(Thing t) {
        if (t.is(LeftDeflector)) {
            Entity left = find(Left), downLeft = find(DownLeft);
            if (left.is(Space) && downLeft.is(Space)) go(DownLeft);
            else moving = false;
        }
        else if (t.is(RightDeflector)) {
            Entity right = find(Right), downRight = find(DownRight);
            if (right.is(Space) && downRight.is(Space)) go(DownRight);
            else moving = false;
        }
        else moving = false;
    }

    void meet(Entity e) { moving = false; }

    private void go(Direction d) {
        moving = true;
        Entity e = find(d);
        advance(Down);
        move(d);
        going(Down);
    }
}
