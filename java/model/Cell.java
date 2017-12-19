package model;

/* Cell class. Free and open source: see licence.txt.

A cell is a primitive organism that occupies a position in a grid. This class is
a base class for entities. It defines some lifecycle methods to be overridden by
specific entity classes, and provides some core methods for entities to use in
order to define their behaviour.

The aim is to make the code in extending classes simple and self-contained. Most
of the methods are delegated to hatchery, state, queue and grid objects.  It is
assumed that the grid has inert wall entities round the edge as sentinels, so
that operations involving neighbours are never out of bounds.  A cell's x, y
fields are private so that they can be kept consistent. This class guarantees
that an entity's coordinates match its grid location.

This class is generic.  The parameter E stands for a game-specific Entity base
class which extends this one.  This eases the implementation of entity classes,
e.g. one entity knows that another has class Entity rather than Cell, without
casting, and can call game-specific methods on it. */

public abstract class Cell<E extends Cell<E>> {
    private char type;
    private Hatchery<E> hatchery;
    private State<E> state;
    private Queue<E> queue;
    private Grid<E> grid;
    public int x, y;

    // Initialize straight after construction.  NOT public.
    void init(
        char t, Hatchery<E> h, State<E> s, Queue<E> q, Grid<E> g, int x0, int y0
    ) {
        type = t;
        hatchery = h;
        state = s;
        queue = q;
        grid = g;
        x = x0;
        y = y0;
    }

    // Lifecycle method to be provided by entity classes.
    // The entity's own initial action at the start of a level.
    public abstract void wake();

    // Lifecycle method to be provided by entity classes.
    // An entity's action when given a turn.
    public abstract void act();

    // Allow one entity to find out the type of another.
    public char type() { return type; }

    // Provide read-only access to the coordinates.  Entities change these only
    // by calling movement methods in this class which guarantee consistency.
    public int x() { return x; }
    public int y() { return y; }

    // Convert this object to the entity class. All actual cell objects are of
    // class E, but the compiler doesn't know that.
    @SuppressWarnings("unchecked")
    private E thisE() { return (E) this; }

    // Check if an entity is of a given type.
    public boolean is(char t) { return type() == t; }

    // Spawn an entity with given code, at the same position, but hidden.
    // Unlike hatching/waking, this is after the level has started.
    public E spawn(char type) {
        E e = hatchery.hatch(type);
        e.init(type, hatchery, state, queue, grid, x, y);
        return e;
    }

    // Core methods to access global variables via the state object.

    public void set(String v, E e) { state.set(v, e); }
    public void set(String v, String s) { state.set(v, s); }
    public void set(String v, int n) { state.set(v, n); }
    public E entity(String v) { return state.entity(v); }
    public String string(String v) { return state.string(v); }
    public int count(String v) { return state.count(v); }
    public void add(String v, int n) { state.add(v, n); }
    public void subtract(String v, int n) { add(v, -n); }

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
    private void move(int u, int v) {
        hide();
        grid.show(u, v, thisE());
        x = u; y = v;
    }
    // Create a background entity behind this one.
    public void background(char code) { grid.back(x, y, spawn(code)); }
    // Find the entity at the back of the grid cell.
    public E background() { return grid.back(x, y); }
    // Replace this entity by one of a different type.
    public void mutate(char t) { hide(); spawn(t).show(); }
    // Move to the back of the grid cell.
    public void moveBack() { hide(); grid.back(x, y, thisE()); }
    // Hide, i.e. go off grid.
    public void hide() { grid.hide(x, y, thisE()); }
    // Show, i.e. stop hiding and go back on the grid.
    public void show() { grid.show(x, y, thisE()); }
    // Show at a given position.
    public void show(int u, int v) {
        if (! hidden()) throw new Error("Already showing");
        grid.show(u,v,thisE());
        x = u; y = v;
    }
    // Check if the entity is off the grid.
    public boolean hidden() { return grid.hidden(x, y, thisE()); }
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
    static class Entity extends Cell<Entity> {
        public void wake() {}
        public void act() {}
    }
    public static void main(String[] args) {
        Entity e = new Entity();
        e.init('@', null, null, null, null, 0, 0);
        claim(e.type() == '@' && e.x() == 0 && e.y() == 0);
        System.out.println("Cell class OK");
    }
}
