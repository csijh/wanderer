package wanderer;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

The Player entity responds to user key presses. */

class Player extends Entity {
    public char code() { return '@'; }

    private Direction go;

    public void hatch() {
        background(Space);
        set(PLAYER, this);
        agent(true);
        set(MESSAGE, "Use arrow keys to move, space to stand still");
    }

    public void act() {
        if (timeout()) return;
        set(MESSAGE, "");
        go = Direction.go(command());
        if (go == Here) return;
        Entity it = find(go);
        meet(it);
    }

    void meetSpace(Entity s) { move(); }

    void meetBaby(Entity b) { end("Killed by the little monsters"); }

    void meetBoulder(Entity b) { if (go == Left || go == Right) push(b); }

    void meetBalloon(Entity b) { if (go == Left || go == Right) push(b); }

    void meetLeftArrow(Entity a) { if (go == Up || go == Down) push(a); }

    void meetRightArrow(Entity a) { if (go == Up || go == Down) push(a); }

    void meetEarth(Entity e) { add(SCORE,1); e.sleep(); move(); }

    void meetStar(Entity e) {
        add(SCORE,10); subtract(STARS,1); e.sleep(); move();
    }

    void meetTime(Entity e) {
        add(SCORE, 5); add(MOVES, 250); e.sleep(); move();
    }

    void meetLandmine(Entity e) { end("Killed by an exploding landmine"); }

    void meetTeleport(Entity t) {
        add(SCORE, 20);
        t.sleep();
        Entity arrival = entity(ARRIVAL);
        excite(arrival.find(UpLeft));
        excite(arrival.find(DownLeft));
        excite(arrival.find(DownRight));
        excite(arrival.find(UpRight));
        excite(this);
        excite(t);
        // Get rid of anything that has landed at the arrival position.
        Entity e = arrival.find(Here);
        while (! e.is(Space)) {
            e.sleep();
            e = arrival.find(Here);
        }
        move(arrival);
    }

    void meetExit(Entity it) {
        int s = count(STARS);
        if (s > 0) return;
        sleep();
        add(SCORE, 250);
        set(MESSAGE, "Success!");
        add(SUCCESS, 1);
        end();
    }

    // Move.
    private void move() {
        advance(go);
        move(go);
    }

    // Move and get killed, with a given message.
    private void end(String message) {
        advance(go);
        move(go);
        set(MESSAGE, message);
        die();
    }

    // Attempt to push a boulder or balloon or arrow.
    private void push(Entity it) {
        Entity next = it.is(Wall) ? it : it.find(go);
        if (!next.is(Space) &&
           (!it.is(Boulder) || !next.is(Monster))) return;
        if (next.is(Monster)) {
            add(SCORE, 100);
            next.sleep();
            next.stop();
        }
        it.move(next);
        advance(go);
        move(go);
        it.queue(true);
    }

    // Check for running out of time.
    private boolean timeout() {
        subtract(MOVES, 1);
        int moves = count(MOVES);
        if (moves >= 0) return false;
        set(MESSAGE, "Killed by running out of time");
        die();
        return true;
    }

    public void die() {
        mutate(Dead);
        end();
    }
}
