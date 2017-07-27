package wanderer;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

The Player entity responds to user key presses. */

class Player extends Entity {
    public char code() { return '@'; }
    void isMetBy(Entity e) { e.meet(this); }

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
        char cmd = command();
        go = direction(cmd);
        if (go == Here) return;
        Entity it = find(go);
        it.isMetBy(this);
    }

    void meet(Space s) { move(); }

    void meet(Baby b) { end("Killed by the little monsters"); }

    void meet(Boulder b) { if (go == Left || go == Right) push(b); }

    void meet(Balloon b) { if (go == Left || go == Right) push(b); }

    void meet(LeftArrow a) { if (go == Up || go == Down) push(a); }

    void meet(RightArrow a) { if (go == Up || go == Down) push(a); }

    void meet(Thing t) {
        if (t.is(Earth)) { add(SCORE,1); t.sleep(); move(); }
        if (t.is(Star)) { add(SCORE,10); subtract(STARS,1); t.sleep(); move(); }
        if (t.is(Time)) { add(SCORE, 5); add(MOVES, 250); t.sleep(); move(); }
        if (t.is(Landmine)) { end("Killed by an exploding landmine"); }
        if (t.is(Teleport)) meetTeleport(t);
        if (t.is(Exit)) meetExit(t);
    }

    void meetTeleport(Thing t) {
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

    void meetExit(Thing it) {
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

    // Convert a command into a direction.
    private Direction direction(char cmd) {
        switch (cmd) {
            case '^': return Up;
            case 'v': return Down;
            case '<': return Left;
            case '>': return Right;
            case '.': return Here;
            default: throw new Error("Bug");
        }
    }

    public void die() {
        mutate(Dead);
        end();
    }
}
