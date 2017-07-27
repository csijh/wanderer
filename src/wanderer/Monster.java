package wanderer;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

The Monster chases and tries to eat the player.  It moves in step with the
player, and moves either vertically or horizontally toward the player, according
both to which distance is less, and to which direction is available to it.
The monster can be killed by a boulder or an arrow. */

class Monster extends Entity {
    public char code() { return 'M'; }
    void isMetBy(Entity e) { e.meet(this); }

    // Make an agent, after the player, but before the baby monsters.
    public void hatch() {
        background(Space);
        Entity player = entity(PLAYER);
        if (player != null) player.stop();
        agent(true);
        if (player != null) player.agent(true);
    }

    public void act() {
        Direction h = Right, v = Down;
        Entity player = entity(PLAYER);
        int dx = distance(this, Right, player);
        int dy = distance(this, Down, player);
        if (dx < 0) { h = Left; dx = -dx; }
        if (dy < 0) { v = Up; dy = -dy; }
        boolean okH = find(h).is(Space) || find(h).is(Player);
        boolean okV = find(v).is(Space) || find(v).is(Player);
        Direction go = Here;
        if (dx > dy && okH) go = h;
        else if (okV) go = v;
        else if (okH) go = h;
        if (go == Here) return;
        Entity target = find(go);
        target.isMetBy(this);
//        if (target.is(Player)) {
//            set(MESSAGE, "Killed by a hungry monster");
//            target.die();
//        }
//        move(go);
    }

    void meet(Player p) {
        set(MESSAGE, "Killed by a hungry monster");
        p.die();
    }

    void meet(Entity e) { move(e); }

    // Find the orthogonal distance between two entities.
    int distance(Entity pe, Direction d, Entity pt) {
        switch (d) {
            case Up: return pe.y() - pt.y();
            case Down: return pt.y() - pe.y();
            case Left: return pe.x() - pt.x();
            case Right: return pt.x() - pe.x();
            default: throw new Error("Bad direction for distance");
        }
    }
}
