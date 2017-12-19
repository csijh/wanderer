package wanderer;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A baby monster. The babies appear from level 10 onwards. They move by left wall
following, ignoring the player except for collisions.  A baby travels over earth
without consuming it, and can end up on top of another entity, specifically
space, earth, a boulder, an arrow or a balloon. Any number of baby monsters can
end up on top of each other in theory (and in practice, there is at least one
level where three end up on top of each other). In addition, a baby monster is
sometimes invisible, as part of its natural behaviour cycle, or because it is
underneath another baby monster. Almost always, the baby eventually gets caught
in a cage, and turns into a star.

The behaviour cycle of babies is as follows. The player moves (and perhaps the
big monster), and all the ensuing triggered events happen. Moving entities
(boulders, arrows, balloons) stop if they hit a baby. After that, each baby gets
a turn to move. It moves as if keeping its left appendage against a wall. When
it has moved, it temporarily goes off grid, and triggers nearby entities (thus
sometimes allowing a moving entity to seem as if it has passed straight
through). While the triggered events happen, moving objects are stopped by
on-grid babies. After that, the baby goes back on the grid (which may leave it
on top of a moving object which happens to have stopped in the baby's grid
cell).

In the original game, babies move even if they are trapped in a single cell and
they have to move on top of something. That behaviour is reproduced here,
because it is needed in some levels such as level 50. However, they are not
allowed to move on top of a wall, so they can't escape from the playing area (as
in the original level 41). */

class Baby extends Entity {
    public char code() { return 'S'; }

    private Direction go;

    // Find a wall or barrier, if any, and set off left along it.
    public void hatch() {
        background(Space);
        if (! canGo(Up)) go = Right;
        else if (! canGo(Right)) go = Down;
        else if (! canGo(Down)) go = Left;
        else if (! canGo(Left)) go = Up;
        else go = Right;
        agent(false);
    }

    // Get to the front, then move, then sleep while the knock-on effects take
    // place, then wake.
    public void act() {
        if (! awake()) { wake(); return; }
        front();
        if (! follow()) return;
        Entity target = find(go);
        meet(target);
    }

    void meetPlayer(Entity e) {
        sleep();
        stop();
        set(MESSAGE, "Killed by the little monsters");
        e.mutate(Dead);
        end();
    }

    void meetCage(Entity e) {
        sleep();
        stop();
        add(SCORE, 20);
        e.mutate(Star);
    }

    void meetEntity(Entity e) {
        queue(true);
        advance(go);
        move(e);
        sleep();
    }

    // Attempt to change direction, in a left-wall-following fashion.
    boolean follow() {
        if (canGo(go.left())) go = go.left();
        else if (canGo(go)) go = go;
        else if (canGo(go.right())) go = go.right();
        else if (! find(go.back()).is(Wall)) go = go.back();
        else return false;
        return true;
    }

    // Move to the front of the grid cell if covered (by sleeping, then waking)
    private void front() {
        sleep();
        wake();
    }

    // Can the baby go in the given direction?
    private boolean canGo(Direction d) {
        Entity e = find(d);
        if (e.is(Space) ||
            e.is(Earth) ||
            e.is(Player) ||
            e.is(Cage) ||
            e.is(Baby)) return true;
        return false;
    }
}
