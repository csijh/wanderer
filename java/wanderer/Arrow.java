package wanderer;
import model.*;

/* Arrow class. Free and open source: see licence.txt.

Supports LeftArrow and RightArrow entities. An Arrow moves Left or Right and
slides over or under a deflector or boulder. It pops a balloon if deflected, or
kills the monster, or kills the player if it is already moving. */

class Arrow extends Entity {
    private boolean moving;
    private Direction normal, deflect;

    public void wake() {
        background(Space);
        moving = false;
        normal = is(LeftArrow) ? Left : Right;
    }

    // Move by one step, possibly deflected or stopped.
    public void act() {
        deflect = normal;
        Entity target = find(normal);
        meet(target);
    }

    void meetSpace(Entity e) { move(); }

    void meetMonster(Entity m) { add(SCORE, 100); m.hide(); m.stop(); move(); }

    void meetBalloon(Entity b) { b.hide(); move(); }

    void meetPlayer(Entity p) {
        if (moving) {
            set(MESSAGE, "Killed by a speeding arrow");
            p.mutate(Dead);
            end();
        }
        else moving = false;
    }

    void meetBoulder(Entity b) {
        deflect = slideBoulder(normal);
        if (deflect == Here) moving = false;
        else move();
    }

    void meetLeftDeflector(Entity e) {
        deflect = deflect(normal, normal == Left ? Down : Up, true);
        if (deflect == Here) moving = false;
        else {
            Entity target = find(deflect);
            if (target.is(Balloon)) target.hide();
            move();
        }
    }

    void meetRightDeflector(Entity e) {
        deflect = deflect(normal, normal == Left ? Up : Down, true);
        if (deflect == Here) moving = false;
        else {
            Entity target = find(deflect);
            if (target.is(Balloon)) target.hide();
            move();
        }
    }

    void meetEntity(Entity e) { moving = false; }

    // Find the direction deflected from the given natural one by a boulder
    private Direction slideBoulder(Direction d) {
        Direction deflect = deflect(d, Up, false);
        if (deflect == Here) deflect = deflect(d, Down, false);
        return deflect;
    }

    // Find the deflected direction to one side, with a boolean for balloons.
    private Direction deflect(Direction d, Direction side, boolean b) {
        Entity s = find(side);
        Direction diag = diagonal(d, side);
        Entity de = find(diag);
        if (s.is(Space) &&
           (de.is(Space) || b && de.is(Balloon))) return diag;
        else return Here;
    }

    // Move in the normal or deflected direction.
    private void move() {
        moving = true;
        advance(normal);
        move(deflect);
        going(normal);
    }

    // Add horizontal and vertical directions to get a diagonal direction
    private Direction diagonal(Direction h, Direction v) {
        if (h == Left && v == Up) return UpLeft;
        else if (h == Left && v == Down) return DownLeft;
        else if (h == Right && v == Up) return UpRight;
        else if (h == Right && v == Down) return DownRight;
        else throw new Error("Bug");
    }
}
