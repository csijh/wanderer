package model;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A cell is a primitive organism that occupies a position in a grid. This class is
a base class for entities. It defines some lifecycle methods to be overridden by
specific entity classes, and provides some core methods for entities to use in
order to define their behaviour.

The aim is to make the code in extending classes simple and self-contained. Many
of the methods are delegated to grid, state and queue objects.  It is assumed
that the grid has inert sentinel wall entities round the edge, so that
operations involving neighbours are never out of bounds.  A cell's x, y fields
are private so that they can be kept consistent. This class guarantees that an
entity's coordinates match its grid location.

This class is generic.  The parameter E stands for the game-specific Entity
class which extends this one.  This eases the implementation of entity classes,
e.g. one entity knows that another has class Entity rather than Cell, without
casting, and can call game-specific methods on it. */

public abstract class Cell<E extends Cell<E>> implements Lifecycle<E> {
    private Level<E> level;
    private int x, y;

    // Initialize straight after construction.  Needed only for samples.
    public void init(Level<E> l) { level = l; }

    // Provide a clone method which can be called on all entity classes.
    @SuppressWarnings("unchecked")
    public E clone() {
        try { return (E) super.clone(); }
        catch (Exception e) { throw new Error(e); }
    }

    // Convert this object to the entity class. All actual cell objects are of
    // class E, but the compiler doesn't know that.
    @SuppressWarnings("unchecked")
    private E thisE() { return (E) this; }

    // Provide convenience synonyms for the standard variable names.
    public static final String
        NAME = Level.NAME, TITLE = Level.TITLE, MOVES = Level.MOVES,
        PLAYER = Level.PLAYER, SCORE = Level.SCORE, SUCCESS = Level.SUCCESS;

    // Provide read-only access to the coordinates.  Entities change these only
    // by calling core methods in this class which guarantee consistency.
    public int x() { return x; }
    public int y() { return y; }

    // Methods in the Lifecycle interface to be provided by entity classes.

    public abstract char code();
    public abstract String image();
    public abstract void hatch();
    public abstract void act();
    public abstract String status();

    // Check if an entity is of a given type.
    public boolean is(char t) { return code() == t; }

    // Spawn an entity with given code, at the same position, but asleep.
    public E spawn(char code) {
        E e = level.spawn(code);
        e.wake(x, y);
        e.sleep();
        return e;
    }

    // Core methods to access global variables via the state object.

    public void set(String v, E e) { level.state.set(v, e); }
    public void set(String v, String s) { level.state.set(v, s); }
    public void set(String v, int n) { level.state.set(v, n); }
    public E entity(String v) { return level.state.entity(v); }
    public String string(String v) { return level.state.string(v); }
    public int count(String v) { return level.state.count(v); }
    public void add(String v, int n) { level.state.add(v, n); }
    public void subtract(String v, int n) { add(v, -n); }

    // Delegate core queue operations to the queue object.

    // Join the queue of active entities. Hi priority means push to the front.
    public void queue(boolean hi) { level.queue.join(thisE(), hi); }
    // Become an autonomous agent. Hi priority means push to the front.
    public void agent(boolean hi) { level.queue.agent(thisE(), hi); }
    // Stop being an agent, and stop acting by leaving the queue.
    public void stop() { level.queue.stop(thisE()); }
    // End the game, by making all entities inactive.
    public void end() { level.queue.end(); }
    // Provide the next command, starting the next round of activity.
    public char command() { return level.queue.command(); }

    // Delegate core positional operations to the grid.

    // Find the neighbour entity in the given direction.
    public E find(Direction d) { return level.grid.front(x + d.x, y + d.y); }
    // Move in the given direction.
    public void move(Direction d) { move(find(d)); }
    // Move to a new position given by another entity.
    public void move(E to) { Cell<E> tc = to; move(tc.x, tc.y); }
    // Move an entity to another position.
    private void move(int u, int v) {
        sleep();
        level.grid.wake(u, v, thisE());
        x = u; y = v;
    }
    // Create a background entity behind this one.
    public void background(char code) { level.grid.back(x, y, spawn(code)); }
    // Find the entity at the back of the grid cell.
    public E background() { return level.grid.back(x, y); }
    // Replace this entity by one of a different type.
    public void mutate(char c) { sleep(); spawn(c).wake(); }
    // Move to the back of the grid cell.
    public void moveBack() { sleep(); level.grid.back(x, y, thisE()); }
    // Go to sleep, off grid.
    public void sleep() { level.grid.sleep(x, y, thisE()); }
    // Wake, i.e. go back on the grid.
    public void wake() { level.grid.wake(x, y, thisE()); }
    // Wake at a given position.
    public void wake(int u, int v) {
        if (awake()) throw new Error("Already awake");
        level.grid.wake(u,v,thisE());
        x = u; y = v;
    }
    // Check if the entity is in the grid.
    public boolean awake() { return level.grid.awake(x, y, thisE()); }
    // Set the grid's changed flag.
    public void changed(boolean b) { level.grid.changed(b); }
    // Check the grid's changed flag.
    public boolean changed() { return level.grid.changed(); }
    // Swap two entities (for purely 2D games).
    public void swap(E with) {
        int x0 = x, y0 = y;
        move(with);
        Cell<E> c = with;
        c.move(x0, y0);
    }

    // Testing
    private static void claim(boolean b) { if (! b) throw new Error("Bug"); }
    static class Item extends Cell<Item> {
        public char code() { return '.'; }
        public String image() { return null; }
        public Item spawn() { return null; }
        public void hatch() {}
        public void act() {}
        public String status() { return null; }
    }
    public static void main(String[] args) {
        Item e = new Item();
        e.init(new Level<Item>(new Item[] {}));
        e.wake(1, 0);
        claim(e.x() == 1);
        claim(e.y() == 0);
        claim(e.awake());
        System.out.println("Cell class OK");
    }
}
