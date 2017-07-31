package wanderer;
import model.*;
import java.lang.reflect.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This acts as a base class for the game-specific Wanderer entities. */

abstract class Entity extends Cell<Entity> {
    // Define convenient synonyms for codes to use as types. Fortunately, Java
    // can distinguish the constants from the class names by context.
    static final char
        Space = '.', Wall = '#', Rock = '=', Earth = ':', Star = '*',
        Cage = '+', Time = 'C', Landmine = '!', Arrival = 'A', Teleport = 'T',
        Exit = 'X', LeftDeflector = '/', RightDeflector = '\\', Dead = '?',
        Boulder = 'O', Balloon = '^', LeftArrow = '<', RightArrow = '>',
        Monster = 'M', Baby = 'S', Player = '@';

    // Use this to switch on entity types, in imitation of the accept method in
    // the visitor pattern, but with no cyclic dependencies, and without having
    // to have a separate class for each entity type.
    void meet(Entity e) {
        switch (e.code()) {
            case Space: meetSpace(e); break;
            case Wall: meetWall(e); break;
            case Rock: meetRock(e); break;
            case Earth: meetEarth(e); break;
            case Star: meetStar(e); break;
            case Cage: meetCage(e); break;
            case Time: meetTime(e); break;
            case Landmine: meetLandmine(e); break;
            case Arrival: meetArrival(e); break;
            case Teleport: meetTeleport(e); break;
            case Exit: meetExit(e); break;
            case LeftDeflector: meetLeftDeflector(e); break;
            case RightDeflector: meetRightDeflector(e); break;
            case Dead: meetDead(e); break;
            case Boulder: meetBoulder(e); break;
            case Balloon: meetBalloon(e); break;
            case LeftArrow: meetLeftArrow(e); break;
            case RightArrow: meetRightArrow(e); break;
            case Monster: meetMonster(e); break;
            case Baby: meetBaby(e); break;
            case Player: meetPlayer(e); break;
            default: throw new Error("Unknown entity type");
        }
    }

    // Override as required in each entity class.  Mimics the visit method in
    // the visitor pattern.
    void meetSpace(Entity e) { meetEntity(e); }
    void meetWall(Entity e) { meetEntity(e); }
    void meetRock(Entity e) { meetEntity(e); }
    void meetEarth(Entity e) { meetEntity(e); }
    void meetStar(Entity e) { meetEntity(e); }
    void meetCage(Entity e) { meetEntity(e); }
    void meetTime(Entity e) { meetEntity(e); }
    void meetLandmine(Entity e) { meetEntity(e); }
    void meetArrival(Entity e) { meetEntity(e); }
    void meetTeleport(Entity e) { meetEntity(e); }
    void meetExit(Entity e) { meetEntity(e); }
    void meetLeftDeflector(Entity e) { meetEntity(e); }
    void meetRightDeflector(Entity e) { meetEntity(e); }
    void meetDead(Entity e) { meetEntity(e); }
    void meetBoulder(Entity e) { meetEntity(e); }
    void meetBalloon(Entity e) { meetEntity(e); }
    void meetLeftArrow(Entity e) { meetEntity(e); }
    void meetRightArrow(Entity e) { meetEntity(e); }
    void meetMonster(Entity e) { meetEntity(e); }
    void meetBaby(Entity e) { meetEntity(e); }
    void meetPlayer(Entity e) { meetEntity(e); }
    void meetEntity(Entity e) {}

    // Define here using reflection, to avoid having to override in each class.
    // (Overridden in the Thing class.)
    public String image() {
        String name = getClass().toString();
        name = name.substring(name.indexOf('.') + 1);
        return "/images/" + name + ".png";
    }

    // Initialise the game state at the start of a level.  Default: do nothing.
    public void hatch() { }

    // Take one step. Default: do nothing.
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
    static final String
        STARS = "STARS", ARRIVAL = "ARRIVAL", MESSAGE = "MESSAGE";

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
