package wanderer;
import model.*;
import java.lang.reflect.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This acts as a base class for the game-specific Wanderer entities. */

abstract class Entity extends Cell<Entity> {
    // Override in each entity class.  Equivalent of accept in visitor pattern.
    // It is abstract to ensure that every entity class defines it.
    abstract void isMetBy(Entity e);

    // Override as required in each entity class.  Equivalent of visit in the
    // visitor pattern.
    void meet(Space e) { meet((Entity) e); }
    void meet(Thing e) { meet((Entity) e); }
    void meet(Boulder e) { meet((Entity) e); }
    void meet(Balloon e) { meet((Entity) e); }
    void meet(LeftArrow e) { meet((Entity) e); }
    void meet(RightArrow e) { meet((Entity) e); }
    void meet(Monster e) { meet((Entity) e); }
    void meet(Baby e) { meet((Entity) e); }
    void meet(Player e) { meet((Entity) e); }
    void meet(Entity e) {}

    // Define convenient synonyms codes to use as types.  Fortunately, Java can
    // distinguish the constants from the class names by context.
    static final char
        Space = '.', Wall = '#', Rock = '=', Earth = ':', Star = '*',
        Cage = '+', Time = 'C', Landmine = '!', Arrival = 'A', Teleport = 'T',
        Exit = 'X', LeftDeflector = '/', RightDeflector = '\\', Dead = '?',
        Boulder = 'O', Balloon = '^', LeftArrow = '<', RightArrow = '>',
        Monster = 'M', Baby = 'S', Player = '@';

    boolean is(char t) { return code() == t; }

    // Define here using reflection, to avoid having to override in each class.
    // (Overridden in the Thing class.)
    public String image() {
        String name = getClass().toString();
        name = name.substring(name.indexOf('.') + 1);
        return "/images/" + name + ".png";
    }

    // Define here using reflection, to avoid having to override in each class.
    // (Overridden in the Thing class.)
    public Entity spawn() {
        try {
            Class<? extends Entity> c = getClass();
            Constructor<? extends Entity> con = c.getDeclaredConstructor();
            con.setAccessible(true);
            return (Entity) con.newInstance();
        }
        catch (Exception err) { throw new Error(err); }
    }

    // Most entities need a space behind them, including deflectors and earth,
    // to trigger movement in nearby entities. A cage later turns into a star,
    // so it increases the number of stars. An arrival entity records the
    // position of the teleport target.
    public void hatch() { }

    // The default is to do nothing.
    public void act() { }

    // Define synonyms for the directions, so that child entity classes don't
    // have to use long names or import anything.
    public static final Direction
        Here = Direction.Here,
        Up = Direction.Up, Down = Direction.Down,
        Right = Direction.Right, Left = Direction.Left,
        UpLeft = Direction.UpLeft, UpRight = Direction.UpRight,
        DownLeft = Direction.DownLeft, DownRight = Direction.DownRight;

    // Define extra state variables: the number of stars left to find, the
    // arrival point for the teleport, and a message to display on screen.
    static final Variable
        STARS = new Variable("STARS"),
        ARRIVAL = new Variable("ARRIVAL"),
        MESSAGE = new Variable("MESSAGE");

    // For each of the four major directions, there is a list of six places to
    // look to see if any entities should be triggered into action.  They are
    // the six cells 'behind' the direction of motion.
    private static Direction[]
        majors = {Down, Left, Right, Up};
    private static Direction[]
        UpList = {Here, Down, Right, Left, DownRight, DownLeft},
        DownList = {Here, Up, Left, Right, UpLeft, UpRight},
        LeftList = {Here, Right, Down, Up, DownRight, UpRight},
        RightList = {Here, Left, Up, Down, UpLeft, DownLeft};

    // Queue up nearby positions to be triggered, before moving in direction d.
    void advance(Direction d) {
        Direction[] list = null;
        switch (d) {
            case Up: list = UpList; break;
            case Down: list = DownList; break;
            case Left: list = LeftList; break;
            case Right: list = RightList; break;
            default: throw new Error("Bad direction for advance()");
        }
        for (int i = list.length-1; i >= 0; i--) {
            excite(find(list[i]));
        }
    }

    // Arrange for a moving entity to keep moving.
    void going(Direction d) {
        excite(find(d));
    }

    // Save up a position for possible later triggering of movement.  Movement
    // of arrows etc. is triggered by checking particular positions to see if
    // entities can move into them. The position is represented by the space
    // entity behind the given entity. So even an inert entity like a deflector
    // needs a space behind it. Positions are pushed onto the front of the list,
    // to simulate the recursion in the original game.  It is important not to
    // excite wall positions on the edge of the grid, to avoid out of bounds
    // errors, so the Wall class should override this.
    void excite(Entity e) {
        Entity s = e.background();
        if (s.is(Space)) s.queue(true);
    }

    // Produce a status string.
    public String status() {
        String lev = "Level " + string(NAME);
        lev += ": " + string(TITLE);
        String message = string(MESSAGE);
        if (message.length() == 0) message = "Stars: " + count(STARS);
        String score = "Score: " + count(SCORE);
        String moves = "Moves: " + count(MOVES);
        String gap = "     ";
        String status = lev + gap + message + gap + score + gap + moves;
        return status;
    }
}
