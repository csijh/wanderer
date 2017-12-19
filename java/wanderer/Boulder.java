package wanderer;
import model.*;

/* Boulder class. Free and open source: see licence.txt.

A Boulder drops, sliding left or right if there is a deflector or another
boulder underneath.  It can kill the player or a monster by falling on it. */

class Boulder extends Entity {
    private boolean moving;

    public void wake() {
        background(Space);
        moving = false;
    }

    public void act() {
        Direction go = Here;
        Entity target = find(Down);
        meet(target);
    }

    void meetSpace(Entity e) { go(Down); }

    void meetMonster(Entity m) {
        add(SCORE, 100);
        m.hide();
        m.stop();
        go(Down);
    }

    void meetPlayer(Entity p) {
        if (! moving) return;
        set(MESSAGE, "Killed by a falling boulder");
        p.mutate(Dead);
        end();
    }

    void meetBoulder(Entity b) {
        Entity left = find(Left), downLeft = find(DownLeft);
        Entity right = find(Right), downRight = find(DownRight);
        if (left.is(Space) && downLeft.is(Space)) go(DownLeft);
        else if (right.is(Space) && downRight.is(Space)) go(DownRight);
        else moving = false;
    }

    void meetLeftDeflector(Entity e) {
        Entity left = find(Left), downLeft = find(DownLeft);
        if (left.is(Space) && downLeft.is(Space)) go(DownLeft);
        else moving = false;
    }

    void meetRightDeflector(Entity e) {
        Entity right = find(Right), downRight = find(DownRight);
        if (right.is(Space) && downRight.is(Space)) go(DownRight);
        else moving = false;
    }

    void meetEntity(Entity e) { moving = false; }

    private void go(Direction d) {
        moving = true;
        Entity e = find(d);
        advance(Down);
        move(d);
        going(Down);
    }
}
