package wanderer;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This is a right facing arrow. It shares the code for LeftArrow. */

class RightArrow extends LeftArrow {
    public char code() { return '>'; }
    void isMetBy(Entity e) { e.meet(this); }
}
