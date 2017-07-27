package wanderer;
import model.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A space is an entity representing the background behind other entities. Any
entity which can move or disappear or trigger other entities into movement
should have a space behind it. Spaces don't move and are used to represent grid
positions, being put into the event queue as a triggering mechanism. A space
'acts' by checking the surrounding entities to see if they can start moving into
(or in the direction of) its grid position. At most one entity is allowed to
move to the position. */

class Space extends Entity {
    public char code() { return '.'; }
    void isMetBy(Entity e) { e.meet(this); }

    private Direction go;

    public void act() {
        Direction[] dirs = {Down, Left, Right, Up};
        for (Direction d : dirs) {
            changed(false);
            Entity it = find(d.back());
            go = d;
            it.isMetBy(this);
            if (changed()) return;
        }
    }

    void meet(Boulder b) { if (go == Down) b.act(); }
    void meet(LeftArrow b) { if (go == Left) b.act(); }
    void meet(RightArrow b) { if (go == Right) b.act(); }
    void meet(Balloon b) { if (go == Up) b.act(); }
}
