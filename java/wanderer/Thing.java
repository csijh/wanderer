package wanderer;

/* Thing class. Free and open source: see licence.txt.

A Thing is an inert entity.  It can be a Wall, Rock, Earth, Star, Cage, Time,
Landmine, Arrival, Teleport, Exit, LeftDeflector, RightDeflector, or Dead.

A Wall is an inert entity used, for example, as a sentinel to form a border
round a level, so that an entity can look at nearby grid cells without using out
of bounds indexes.  A Rock is the same, just providing visual variety. Earth is
consumed when the player moves onto it, but baby monsters move over it. A Star
is a treasure that the player has to collect before exiting. A Cage traps a baby
monster. A Time capsule provides 250 extra moves when the player collects it. A
Landmine explodes and kills the player if the player walks on it. An Arrival
entity, at most one per level, marks the arrival point for any teleports. A
Teleport takes the player to the arrival point. The Exit in each level allows
the player to end the level, when all the stars have been collected. */

class Thing extends Entity {
    // Override to deal with multiple types of Thing.
    public Entity spawn() { return new Thing(); }

    // Define wake to deal with multiple types of Thing.
    public void wake() {
        if (! is(Space) && ! is(Wall)) background(Space);
        if (is(Star) || is(Cage)) add(STARS, 1);
        else if (is(Arrival)) {
            set(ARRIVAL, this);
            hide();
        }
    }
}
