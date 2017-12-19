package wanderer;
import model.*;

/* Space class. Free and open source: see licence.txt.

A space is an entity representing the background behind other entities. Any
entity which can move or disappear or trigger other entities into movement
should have a space behind it. Spaces don't move and are used to represent grid
positions, being put into the event queue as a triggering mechanism. A space
'acts' by checking the surrounding entities to see if they can start moving into
(or in the direction of) its grid position. At most one entity is allowed to
move to the position. */

class Space extends Entity {
    private Direction go;

    public void act() {
        Direction[] dirs = {Down, Left, Right, Up};
        for (Direction d : dirs) {
            changed(false);
            Entity it = find(d.back());
            go = d;
            meet(it);
            if (changed()) return;
        }
    }

    void meetBoulder(Entity e) { if (go == Down) e.act(); }
    void meetLeftArrow(Entity e) { if (go == Left) e.act(); }
    void meetRightArrow(Entity e) { if (go == Right) e.act(); }
    void meetBalloon(Entity e) { if (go == Up) e.act(); }
}
