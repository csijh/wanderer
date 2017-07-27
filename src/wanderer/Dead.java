package wanderer;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

An inert version of the player with a different image. */

class Dead extends Entity {
    public char code() { return '?'; }
    void isMetBy(Entity e) { e.meet(this); }
}
