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

public abstract class Cell<E extends Cell<E>> implements Cloneable {
    private Grid<E> grid;
    private State<E> state;
    private Queue<E> queue;
    private int x, y;

    // Make sure the clone method can be called on all entity classes.
    @SuppressWarnings("unchecked")
    public E clone() {
        try { return (E) super.clone(); }
        catch (Exception e) { throw new Error(e); }
    }

    // Initialise an entity using a method rather than a constructor,
    // so that extending classes don't have to provide a constructor.
    void init(Grid<E> g, State<E> s, Queue<E> q, int x0, int y0) {
        grid = g;
        state = s;
        queue = q;
        x = x0;
        y = y0;
    }

    // Convert this object to the entity class. All actual cell objects are of
    // class E, but the compiler doesn't know that.
    @SuppressWarnings("unchecked")
    private E thisE() { return (E) this; }

    // Provide convenience synonyms for the standard variable names.
    public static final String
        NAME = "NAME", TITLE = "TITLE", MOVES = "MOVES", PLAYER = "PLAYER",
        SCORE = "SCORE", SUCCESS = "SUCCESS";

    // Provide read-only access to the coordinates.  Entities change these only
    // by calling core methods in this class which guarantee consistency.
    public int x() { return x; }
    public int y() { return y; }

    // Lifecycle methods to be provided by entity classes.

    // Find the character code used in level files for this type of entity.
    public abstract char code();
    // Find the file path to the image for this type of entity.
    public abstract String image();
    // Create an uninitialized entity of the same type as this one.
//    public abstract E spawn();
    // Make any desired changes at the start of a level.
    public abstract void hatch();
    // Make one basic action during play.
    public abstract void act();
    // Provide a status string for the user interface.
    public abstract String status();

    // Core methods to access global variables via the state object.

    public void set(String v, E e) { state.set(v, e); }
    public void set(String v, String s) { state.set(v, s); }
    public void set(String v, int n) { state.set(v, n); }
    public E entity(String v) { return state.entity(v); }
    public String string(String v) { return state.string(v); }
    public int count(String v) { return state.count(v); }
    public void add(String v, int n) { state.set(v, state.count(v) + n); }
    public void subtract(String v, int n) { add(v, -n); }
    // Spawn an entity with given code, at the same position, but asleep.
    public E spawn(char code) {
        E e = state.sample(code).clone();
        e.init(grid, state, queue, x, y);
        return e;
    }
    // Check if an entity is of a given type.
    public boolean is(char t) { return code() == t; }

    // Delegate core queue operations to the queue object.

    // Join the queue of active entities. Hi priority means push to the front.
    public void queue(boolean hi) { queue.join(thisE(), hi); }
    // Become an autonomous agent. Hi priority means push to the front.
    public void agent(boolean hi) { queue.agent(thisE(), hi); }
    // Stop being an agent, and stop acting by leaving the queue.
    public void stop() { queue.stop(thisE()); }
    // End the game, by making all entities inactive.
    public void end() { queue.end(); }
    // Provide the next command, starting the next round of activity.
    public char command() { return queue.command(); }

    // Delegate core positional operations to the grid.

    // Find the neighbour entity in the given direction.
    public E find(Direction d) { return grid.front(x + d.x, y + d.y); }
    // Move in the given direction.
    public void move(Direction d) { move(find(d)); }
    // Move to a new position given by another entity.
    public void move(E to) { Cell<E> tc = to; move(tc.x, tc.y); }
    // Move an entity to another position.
    private void move(int u, int v) { grid.move(x,y,u,v,thisE()); x=u; y=v; }
    // Create a background entity behind this one.
    public void background(char code) { grid.back(x, y, spawn(code)); }
    // Find the entity at the back of the grid cell.
    public E background() { return grid.back(x, y); }
    // Replace this entity by one of a different type.
    public void mutate(char c) { sleep(); spawn(c).wake(); }
    // Move to the back of the grid cell.
    public void moveBack() { sleep(); grid.back(x, y, thisE()); }
    // Go to sleep, off grid.
    public void sleep() { grid.sleep(x, y, thisE()); }
    // Wake, i.e. go back on the grid.
    public void wake() { grid.wake(x, y, thisE()); }
    // Check if the entity is in the grid.
    public boolean awake() { return grid.awake(x, y, thisE()); }
    // Set the grid's changed flag.
    public void changed(boolean b) { grid.changed(b); }
    // Check the grid's changed flag.
    public boolean changed() { return grid.changed(); }
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
        e.init(null, null, null, 2, 3);
        claim(e.x() == 2);
        claim(e.y() == 3);
        System.out.println("Cell class OK");
    }
}
