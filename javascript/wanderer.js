/* Play wanderer.  The code is adapted from Java to work in HTML 5 Canvas.  The
the width, height and pixel size of the play area are global constants.  The
global variables are the display, which knows about drawing on the screen and
capturing key presses, the grid, which holds entities, the level, which holds
information about the state of play on the current level, and the current level
number. */

var width = 42, height = 18, pixels = 20;
var display, grid, level;
var levelNumber = 1;

// Initalize and start the game.

window.onload = start;
function start()
{
    display = new Display();
    display.init();
    grid = new Grid();
    level = new Level();
    level.load(levelNumber);
}

/* Directions are global constants.  They can be converted to and from dx and
dy pairs. */

var Here = { dx:0, dy:0 };
var Up = { dx:0, dy:-1 };
var Down = { dx:0, dy:1 };
var Right = { dx:1, dy:0 };
var Left = { dx:-1, dy:0 };
var UpLeft = { dx:-1, dy:-1 };
var UpRight = { dx:1, dy:-1 };
var DownLeft = { dx:-1, dy:1 };
var DownRight = { dx:1, dy:1 };

Here.opposite = Here;
Up.opposite = Down;
Down.opposite = Up;
Left.opposite = Right;
Right.opposite = Left;
UpLeft.opposite = DownRight;
UpRight.opposite = DownLeft;
DownLeft.opposite = UpRight;
DownRight.opposite = UpLeft;

function direction(dx, dy)
{
    switch (dx)
    {
    case -1:
        switch (dy)
        {
        case -1: return UpLeft;
        case 0: return Left;
        case 1: return DownLeft;
        }
        break;
    case 0:
        switch (dy)
        {
        case -1: return Up;
        case 0: return Here;
        case 1: return Down;
        }
        break;
    case 1:
        switch (dy)
        {
        case -1: return UpRight;
        case 0: return Right;
        case 1: return DownRight;
        }
        break;
    }
    return null;
}

/* Create a display object.  Icons are fetched from named img elements in the
document.  Animation is handled using an event queue, which holds requests to
draw icons, requests to write text, outstanding key presses, and pauses between
frames.  Each time the tick method is called, it executes all the events on the
queue up to the first pause. */

function Display()
{
    var self = this;
    var events = new Queue();
    var canvas;
    var ctx;
    var messages = ["", "", "", ""];
    var KEY=0, DRAW=1, WRITE=2, SLEEP=3;
    var timer;

//    init();

    this.init = init;
    this.fetchIcon = fetchIcon;
    this.tick = tick;
    this.press = press;
    this.restart = restart;
    this.next = next;
    this.selectLevel = selectLevel;
    this.selectSpeed = selectSpeed;
    this.draw = draw;
    this.write = write;
    this.sleep = sleep;
    this.message = message;

    function init()
    {
        canvas = document.createElement("canvas");
        canvas.id = "canvas";
        ctx = canvas.getContext("2d");
        if (! canvas || ! ctx) throw "No Canvas";
        canvas.width = width * pixels;
        canvas.height = (height + 1) * pixels;
        var old = document.getElementById("canvas");
        old.parentNode.replaceChild(canvas, old);
        ctx.fillStyle = "rgb(255, 255, 255)";
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.font = "16px sans-serif";
        writeText("");

        function thisPress(ev) { display.press(ev); return false; }
        document.onkeydown = thisPress;
        var restartButton = document.getElementById("restart");
        function thisRestart(ev) { display.restart(); return false; }
        restartButton.onclick = thisRestart;
        var nextButton = document.getElementById("next");
        function thisNext(ev) { display.next(); return false; }
        nextButton.onclick = thisNext;
        var levelSelect = document.getElementById("wandererLevel");
        function thisLevel(ev) { display.selectLevel(); return false; }
        levelSelect.onchange = thisLevel;
        var speedSelect = document.getElementById("speed");
        function thisSpeed(ev) { display.selectSpeed(); return false; }
        speedSelect.onchange = thisSpeed;

        function thisTick() { display.tick(); }
        timer = setInterval(thisTick, 33);
    }

    function restart()
    {
        clearInterval(timer);
        start();
    }

    function next()
    {
        var levelSelect = document.getElementById("wandererLevel");
        var nLevels = levelSelect.options.length;
        if (levelNumber >= nLevels) return;
        levelNumber++;
        levelSelect.selectedIndex = levelNumber - 1;
        clearInterval(timer);
        start();
    }

    function selectLevel()
    {
        var levelSelect = document.getElementById("wandererLevel");
        var text = levelSelect.options[levelSelect.selectedIndex].value;
        levelNumber = parseInt(text);
        clearInterval(timer);
        start();
    }

    function selectSpeed()
    {
        var speedSelect = document.getElementById("speed");
        var text = speedSelect.options[speedSelect.selectedIndex].value;
        speedNumber = parseInt(text);
        clearInterval(timer);
        function thisTick() { display.tick(); }
        setInterval(thisTick, 1000/speedNumber);
    }

    function fetchIcon(name)
    {
        return document.getElementById(name);
    }

    function drawIcon(icon, x, y)
    {
        ctx.drawImage(icon, x * pixels, y * pixels);
    }

    function writeText(text)
    {
        ctx.fillStyle = "rgb(200, 200, 255)";
        ctx.fillRect(0, canvas.height - pixels, canvas.width, canvas.height);
        ctx.fillStyle = "rgb(0, 0, 0)";
        ctx.fillText(text, 10, canvas.height-5);
    }

    function press(ev)
    {
        if (!ev) ev = window.event;
//        if (ev.preventDefault) ev.preventDefault();
//        ev.returnValue = false;
        var key = ev.keyCode;
        var event = {type:KEY, key:key};
        events.add(event);
    }

    // These should be declared const, but const is not standard (e.g. IE8).
    var leftArrow = 37, upArrow = 38, rightArrow = 39, downArrow = 40;
    var letterA = 65, letterW = 87, letterD = 68, letterS = 83;
    var letterH = 72, letterK = 75, letterL = 76, letterJ = 74;
    var spaceBar = 32;

    function tick()
    {
        while (events.size() > 0)
        {
            var e = events.take();
            switch (e.type)
            {
            case KEY:
                switch (e.key)
                {
                case leftArrow: case letterA: case letterH:
                    level.run(Left); break;
                case upArrow: case letterW: case letterK:
                    level.run(Up); break;
                case rightArrow: case letterD: case letterL:
                    level.run(Right); break;
                case downArrow: case letterS: case letterJ:
                    level.run(Down); break;
                case spaceBar: level.run(Here); break;
                case 'X': alert("X pressed");
                }
                break;
            case DRAW:
                drawIcon(e.icon, e.x, e.y);
                break;
            case WRITE:
                writeText(e.text);
                break;
            case SLEEP: return;
            }
        }
    }

    function draw(x, y, icon)
    {
        var event = {type:DRAW, x:x, y:y, icon:icon};
        events.add(event);
    }

    function write(text)
    {
        var event = {type:WRITE, text:text};
        events.add(event);
    }

    function sleep()
    {
        var event = {type:SLEEP};
        events.add(event);
    }

    function message(i, text)
    {
        messages[i] = text;
        var text = messages[0];
        for (var i=1; i<4; i++) text += "     " + messages[i];
        write(text);
    }
}

/* A queue is implemented using an array-like object with the initial items
deleted.  It is bullet-proof because you can't access the contents other than
via the methods.  It is compact because a JavaScript array is really just a
map, and deleting map entries really does recover their space.  It is efficient
because there is no shuffling of items, as in some other implementations. */

function Queue()
{
    var start = 0;
    var end = 0;
    var items = new Object();

    this.add = add;
    this.take = take;
    this.size = size;

    function add(x)
    {
        items[end++] = x;
    }

    function take()
    {
        if (start == end) return undefined;
        var x = items[start];
        delete items[start];
        start++;
        return x;
    }

    function size()
    {
        return end - start;
    }
}

/* A Level object represents the state of play within the current level.  The
text which defines a level is fetched from a named (but hidden) element in the
document.  The triggered events are stored in a stack rather than a queue to
mimic the original game. */

function Level(levelNumber)
{
    var self = this;
    var events;
    var player;
    var monster;
    var babies;
    var arrival;
    var score;
    var moves, maxMoves;
    var treasure, maxTreasure;
    var win;

    this.load = load;
    this.getPlayer = getPlayer;
    this.getMonster = getMonster;
    this.setMonster = setMonster;
    this.getBabies = getBabies;
    this.addBaby = addBaby;
    this.delBaby = delBaby;
    this.getArrival = getArrival;
    this.setArrival = setArrival;
    this.addScore = addScore;
    this.addMoves = addMoves;
    this.addTreasure = addTreasure;
    this.end = end;
    this.run = run;
    this.enqueue = enqueue;

    // Getters and setters etc.

    function getPlayer() { return player; }
    function getMonster() { return monster; }
    function setMonster(m) { monster = m; }
    function getBabies() { return babies.slice(0); }
    function addBaby(b) { babies.push(b); }
    function delBaby(b)
    {
        var index = -1;
        for (var i=0; i<babies.length; i++)
        {
            if (babies[i].id() == b.id()) index = i;
        }
        if (index < 0) return;
        babies.splice(index, 1);
    }
    function hasBaby(b)
    {
        for (var i=0; i<babies.length; i++)
        {
            if (babies[i].id() == b.id()) return true;
        }
        return false;
    }
    function getArrival() { return arrival; }
    function setArrival(entity) { arrival = grid.position(entity); }
    function addScore(n)
    {
        score += n;
        display.message(2, "Score: " + score);
    }
    function addMoves(n)
    {
        moves += n;
        if (n > 0) maxMoves += n;
        if (n >= 0) return moves;
        if (maxMoves <= 0) display.message(3, "Moves: " + (maxMoves-moves));
        else display.message(3, "Moves: " + (maxMoves-moves) + "/" + maxMoves);
        return moves;
    }
    // Add to (or subtract from) the amount of treasure to be found.
    function addTreasure(n)
    {
        treasure += n;
        if (treasure > maxTreasure) maxTreasure = treasure;
        var found = maxTreasure-treasure;
        display.message(1, "Stars found: " + found + "/" + maxTreasure);
        return treasure;
    }

    // The current entity calls this to queue up the neighbouring cell in the
    // given direction as a later event.

    function enqueue(it, dir)
    {
        if (grid.nowhere(it)) return;
        var pos = grid.position(grid.find(it, dir));
        events.push(pos);
    }

    // Report the end of the level, either with a string to say why the player
    // died, or with the empty string to report successful completion.

    function end(text)
    {
        if (text == "")
        {
            display.message(1, "Success!");
            win = 1;
        }
        else
        {
            display.message(1, text);
            win = -1;
        }
        events = new Array();
    }

    // Read in the level file for the n'th level, to fill in the grid.

    function load(levelNumber)
    {
        events = new Array();
        player = null;
        monster = null;
        babies = new Array(0);
        arrivalx = 0;
        arrivaly = 0;
        score = 0;
        moves = 0;
        maxMoves = 0;
        treasure = 0;
        maxTreasure = 0;
        win = 0;

        var id = "level." + levelNumber;
        var elt = document.getElementById(id);
        var text = elt.childNodes[0].nodeValue;
        if (text.charAt(0) == '\n') text = text.substring(1);
        for (var x=0; x<width; x++)
        {
            for (var y=0; y<height; y++)
            {
                var ch = text.charAt(x+y*(width+1));
                var it = new Entity(ch);
                grid.set(x, y, it);
                if (it.isPlayer()) player = it;
            }
        }
        text = text.substring(height * (width+1));
        var lines = text.split('\n');
        title = lines[0];
        var m = parseInt(lines[1]);
        if (m) addMoves(m);
        grid.init();
        display.message(0, "Level " + levelNumber + ": " + title);
        display.message(1, "Use arrow keys to move, space to stand still");
        display.message(2, "");
        display.message(3, "");
    }

    // Run the game, for one step by the player.  Return -1 if the player is
    // dead, 0 if the game is still running, and 1 if the player has succeeded.

    function run(d)
    {
        action(player, d);
        if (monster != null) action(monster, d);
        var copy = getBabies();
        for (var i=0; i<copy.length; i++)
        {
            var baby = copy[i];
            action(baby, d);
            if (hasBaby(baby)) baby.babyShow();
//            if (babies[i].id() == baby.id()) baby.babyShow();
        }
        if (win == 0) { addTreasure(0); addScore(0); }
        return win;
    }

    // Get an entity to take one action, and deal with all the consequences.

    function action(actor, d)
    {
        if (win != 0) return;
        actor.act(d);
        while (events.length > 0)
        {
            if (win != 0) break;
            var pos = events.pop();
            var it = grid.get(pos.x, pos.y);
            it.trigger();
        }
        events = new Array();
    }
}

/* The grid object keeps track of where entities are. It has a matrix to find
an entity given an (x,y) position, and also a map to find the (x,y) position of
a given entity.  It guarantees to keep these two structures consistent.  The
grid is the only object which knows positions and carries out coordinate
calculations. */

function Grid()
{
    var self = this;
    var entities = new Array(width);
    var positions = new Object();

    for (var x=0; x<width; x++) entities[x] = new Array(height);

    this.init = init;
    this.get = get;
    this.set = set;
    this.position = position;
    this.distance = distance;
    this.find = find;
    this.replace = replace;
    this.nowhere = nowhere;

    function init()
    {
        for (var x=0; x<width; x++)
        {
            for (var y=0; y<height; y++)
            {
                entities[x][y].init();
            }
        }
    }

    function get(x, y)
    {
        return entities[x][y];
    }

    function set(x, y, it)
    {
        if (positions[it.id()]) alert("Entity in two places");
        var old = entities[x][y];
        entities[x][y] = it;
        positions[it.id()] = { x:x, y:y };
        if (old) delete positions[old.id()];
        display.draw(x, y, it.icon());
    }

    function position(it) { return positions[it.id()]; }

    // Find the horizontal or vertical distance from one entity to another.

    function distance(e1, e2, horizontal)
    {
        var p1 = position(e1);
        var p2 = position(e2);
        if (horizontal) return p2.x - p1.x;
        else return p2.y - p1.y;
    }

    // Find the neighbouring entity in the given direction.
    // It is assumed that bounds checking isn't needed.

    function find(it, dir)
    {
        var pos = positions[it.id()];
        var x = pos.x + dir.dx;
        var y = pos.y + dir.dy;
        return entities[x][y];
    }

    // Replace an entity in the grid with another entity which isn't currently
    // in the grid.

    function replace(it, other)
    {
        var pos = position(it);
        set (pos.x, pos.y, other);
    }

    // Check whether an entity is nowhere, i.e. off the grid.

    function nowhere(it)
    {
        if (positions[it.id()]) return false;
        return true;
    }
}

/* An entity is created from a character in a level file, which determines its
type.  The behaviour of an entity is determined by a small number of methods,
which are overridden for specific entity types. */

var nextEntityId = 0;

var SPACE = { code:' ', name:"space" };
var WALL = { code:'#', name:"wall" };
var ROCK = { code:'=', name:"rock" };
var EARTH = { code:':', name:"earth" };
var RIGHT_DEFLECTOR = { code:'\\', name:"rightdeflector" };
var LEFT_DEFLECTOR = { code:'/', name:"leftdeflector" };
var TREASURE = { code:'*', name:"treasure" };
var CAGE = { code:'+', name:"cage" };
var LANDMINE = { code:'!', name:"landmine" };
var BOMB = { code:'B', name:"bomb" };
var TELEPORT = { code:'T', name:"teleport" };
var TIME_CAPSULE = { code:'C', name:"time" };
var EXIT = { code:'X', name:"exit" };
var ARRIVAL = { code:'A', name:"space" };
var BOULDER = { code:'O', name:"boulder" };
var LEFT_ARROW = { code:'<', name:"leftarrow" };
var RIGHT_ARROW = { code:'>', name:"rightarrow" };
var BALLOON = { code:'^', name:"balloon" };
var PLAYER = { code:'@', name:"player" };
var DEAD = { code:'?', name:"dead" };
var MONSTER = { code:'M', name:"monster" };
var BABY_MONSTER = { code:'S', name:"baby" };
var THINGY = { code:'~', name:"thingy" };

var types =
[ SPACE, WALL, ROCK, EARTH, RIGHT_DEFLECTOR, LEFT_DEFLECTOR, TREASURE, CAGE,
  LANDMINE, BOMB, TELEPORT, TIME_CAPSULE, EXIT, ARRIVAL, BOULDER, LEFT_ARROW,
  RIGHT_ARROW, BALLOON, PLAYER, DEAD, MONSTER, BABY_MONSTER, THINGY
];

function Entity(code)
{
    var self = this;
    var type;
    var myId = nextEntityId++;
    var myIcon;
    var isMoving = false;
    var babyDirection, babyRecent, babyUnder;

    this.id = id;
    this.icon = icon;
    this.init = init;
    this.act = act;
    this.moving = moving;
    this.collide = collide;
    this.isPlayer = isPlayer;
    this.trigger = trigger;
    this.is = is;
    this.babyHide = babyHide;
    this.babyShow = babyShow;

    for (var i=0; i<types.length; i++)
    {
        if (code == types[i].code) type = types[i];
    }
    myIcon = display.fetchIcon(type.name);

    if (is(EXIT)) this.collide = exitCollide;
    if (is(SPACE)) this.collide = spaceCollide;
    if (is(EARTH)) this.collide = earthCollide;
    if (is(PLAYER)) this.collide = playerCollide;
    if (is(PLAYER)) this.act = playerAct;
    if (is(TREASURE)) this.init = treasureInit;
    if (is(TREASURE)) this.collide = treasureCollide;
    if (is(CAGE)) this.init = cageInit;
    if (is(CAGE)) this.collide = cageCollide;
    if (is(LANDMINE)) this.collide = landmineCollide;
    if (is(TELEPORT)) this.collide = teleportCollide;
    if (is(ARRIVAL)) this.init = arrivalInit;
    if (is(TIME_CAPSULE)) this.collide = timeCapsuleCollide;
    if (is(BOULDER)) this.act = boulderAct;
    if (is(LEFT_ARROW)) this.act = leftArrowAct;
    if (is(RIGHT_ARROW)) this.act = rightArrowAct;
    if (is(BALLOON)) this.act = balloonAct;
    if (is(BALLOON)) this.collide = balloonCollide;
    if (is(MONSTER)) this.init = monsterInit;
    if (is(MONSTER)) this.collide = monsterCollide;
    if (is(MONSTER)) this.act = monsterAct;
    if (is(BABY_MONSTER)) this.init = babyInit;
    if (is(BABY_MONSTER)) this.collide = babyCollide;
    if (is(BABY_MONSTER)) this.act = babyAct;

    function id() { return myId; }
    function icon() { return myIcon; }

    // These are the default definitions of the methods which determine the
    // behaviour of entities.  Each specific entity class accepts these
    // defaults, or overrides these methods to define its own behaviour.

    // Carry out initialisation at the start of a level.

    function init() { }

    // Take a single action, and return whether or not anything happened.

    function act(dir) { return false; }

    // Check whether the entity is moving.

    function moving() { return isMoving; }

    // Carry out the action needed when another entity lands on this one.

    function collide(other) { }

    // Check whether the entity is the player.

    function isPlayer() { return type == PLAYER; }

    // Check whether the type of an entity is one of the given types.

    function is()
    {
        for (var i=0; i<arguments.length; i++)
        {
            if (type.code == arguments[i].code) return true;
        }
        return false;
    }

    // For each of the four major directions, there is a list of six places to
    // look to see if any entities should be triggered into action.

    var
        majors = [Down, Left, Right, Up],
        upList = [Here, Down, Right, Left, DownRight, DownLeft],
        downList = [Here, Up, Left, Right, UpLeft, UpRight],
        leftList = [Here, Right, Down, Up, DownRight, UpRight],
        rightList = [Here, Left, Up, Down, UpLeft, DownLeft];

    // The trigger method is called when a target entity is triggered by the
    // movement of a nearby entity.  The four major directions are checked, to
    // look for the first entity which can move onto or slide past the target.
    // Walls are explicitly ignored, to avoid out-of-bounds tests.  The other
    // methods are primitive actions which an entity can carry out.

    function trigger()
    {
        if (is(WALL)) return;
        for (var i=0; i<majors.length; i++)
        {
            var dir = majors[i];
            var candidate = grid.find(self, dir.opposite);
            if (candidate.is(MONSTER)) continue;
            if (candidate.is(BABY_MONSTER)) continue;
            if (! candidate.is(PLAYER))
            {
                var acted = candidate.act(dir);
                if (acted) break;
            }
        }
    }

    // An Entity calls this just before it moves, to queue up nearby cells
    // to be triggered later.  The nearby cells are the six cells 'behind'
    // the direction of motion.  (The cells are handled in reverse order
    // compared to the original program, because we are using a stack, not
    // recursion.)

    function start(d)
    {
        if (d == Here) return;
        var list = null;
        switch (d)
        {
        case Up: list = upList; break;
        case Down: list = downList; break;
        case Left: list = leftList; break;
        case Right: list = rightList; break;
        default: throw "Bad direction for moveAndTrigger";
        }
        for (var i = list.length-1; i >= 0; i--)
        {
            level.enqueue(self, list[i]);
        }
    }

    // An Entity calls this just before it moves, to queue up nearby cells
    // to be triggered later.  The nearby cells are the six cells 'behind'
    // the direction of motion.  (The cells are handled in reverse order
    // compared to the original program, because we are using a stack, not
    // recursion.)

    function moveAndTrigger(md, d)
    {
        if (d == Here) return;
        var list = null;
        switch (d)
        {
        case Up: list = upList; break;
        case Down: list = downList; break;
        case Left: list = leftList; break;
        case Right: list = rightList; break;
        default: throw "Bad direction for moveAndTrigger";
        }
        for (var i = list.length-1; i >= 0; i--)
        {
            level.enqueue(self, list[i]);
        }
        move(self, md);
    }

    // These are utility functions for entities.

    function space() { return new Entity(' '); }
    function dead() { return new Entity('?'); }
    function treasure() { return new Entity('*'); }

    // Move by one step in the given direction.  Ask the entity you are moving
    // onto to deal with the collision.

    function move(it, dir)
    {
        var target = grid.find(it, dir);
        grid.replace(it, space());
        target.collide(it);
        display.sleep();
    }

// ----------------------------------------------------------------------------
    // The Exit is where the player leaves the level, only accessible once all
    // the treasure has been found.

    function exitCollide(player)
    {
        if (level.addTreasure(0) == 0) level.addScore(250);
        level.end("");
    }

// ----------------------------------------------------------------------------
    // A space is just a blank areas where any entity can go.

    function spaceCollide(other)
    {
        grid.replace(self, other);
    }

// ----------------------------------------------------------------------------
    // Earth can only be waded through by the player or travelled over by a
    // baby monster.

    function earthCollide(other)
    {
        if (other.is(PLAYER)) level.addScore(1);
        grid.replace(self, other);
    }

// ----------------------------------------------------------------------------
    // The Player entity responds to user key presses, moving in the given
    // direction, possibly pushing a boulder, arrow or ballon.

    function playerViable(d)
    {
        var target = grid.find(self, d);
        if (target.is(SPACE, EARTH, TREASURE)) return true;
        if (target.is(LANDMINE, TELEPORT, TIME_CAPSULE)) return true;
        if (target.is(BABY_MONSTER)) return true;
        return (target.is(EXIT) && level.addTreasure(0) == 0);
    }

    function playerCollide(other)
    {
        grid.replace(self, dead());
        if (other.is(LANDMINE)) level.end("Killed by an exploding landmine");
        if (other.is(BOULDER)) level.end("Killed by a falling boulder");
        if (other.is(LEFT_ARROW)) level.end("Killed by a speeding arrow");
        if (other.is(RIGHT_ARROW)) level.end("Killed by a speeding arrow");
        if (other.is(BOMB)) level.end("Killed by an exploding bomb");
        if (other.is(MONSTER)) level.end("Killed by a hungry monster");
        if (other.is(BABY_MONSTER)) level.end("Killed by the little monsters");
        if (other.is(PLAYER)) level.end("Killed by running out of time");
    }

    function playerAct(dir)
    {
        var acted = false;
        var it = grid.find(self, dir);
        var next = it.is(WALL) ? it : grid.find(it, dir);
        if ((dir == Left || dir == Right) && it.is(BOULDER) &&
            next.is(SPACE, BOMB, MONSTER))
        {
            move(it, dir);
            moveAndTrigger(dir, dir);
            it.act(Down);
            acted = true;
        }
        else if ((dir == Up || dir == Down) &&
             it.is(LEFT_ARROW, RIGHT_ARROW) && next.is(SPACE))
        {
            move(it, dir);
            moveAndTrigger(dir, dir);
            if (it.is(LEFT_ARROW)) it.act(Left);
            else it.act(Right);
            acted = true;
        }
        else if ((dir == Left || dir == Right) && it.is(BALLOON) &&
            next.is(SPACE))
        {
            move(it, dir);
            moveAndTrigger(dir, dir);
            it.act(Up);
            acted = true;
        }
        else if (playerViable(dir))
        {
            moveAndTrigger(dir, dir);
            acted = true;
        }
        var moves = level.addMoves(-1);
        if (moves == 0) playerCollide(self);
        return acted;
    }

// ----------------------------------------------------------------------------
    // Treasure is picked up by the player.

    function treasureInit()
    {
        level.addTreasure(+1);
    }

    function treasureCollide(player)
    {
        level.addTreasure(-1);
        level.addScore(10);
        grid.replace(self, player);
    }

// ----------------------------------------------------------------------------
    // A cage captures a baby monster and turns into treasure.

    function cageInit()
    {
        level.addTreasure(+1);
    }

    function cageCollide(baby)
    {
        level.delBaby(baby);
        level.addScore(20);
        grid.replace(self, treasure());
    }

// ----------------------------------------------------------------------------
    // A landmine can only be stepped on by the player.  The responsibility for
    // the incident is passed back to the player, so that the player is
    // responsible for reporting all modes of death.

    function landmineCollide(other)
    {
        grid.replace(self, other);
        if (other.is(PLAYER)) other.collide(self);
    }

// ----------------------------------------------------------------------------
    // A Boulder drops, sliding left or right if there is a deflector or
    // another boulder underneath.  It can kill the player or a monster,
    // by falling on it.

    // Test whether the boulder can go in the given direction.  The boulder
    // only affects the entity immediately underneath if it is already moving.

    function boulderViable(d)
    {
        var target = grid.find(self, d);
        if (target.is(BOMB, MONSTER, SPACE)) return true;
        return (isMoving && target.is(PLAYER));
    }

    // Move down, sliding left or right if there is a deflector or another
    // boulder underneath, and possibly killing or blowing up what it lands on.

    function boulderAct(dir)
    {
        if (dir != Down) return false;
        var t = grid.find(self, Down);
        if (boulderViable(Down))
        {
            moveAndTrigger(Down, Down);
            isMoving = true;
        }
        else if (t.is(BOULDER) && grid.find(self,Left).is(SPACE) &&
            grid.find(self,DownLeft).is(SPACE))
        {
            moveAndTrigger(DownLeft, Down);
            isMoving = true;
        }
        else if (t.is(BOULDER) && grid.find(self,Right).is(SPACE) &&
            grid.find(self,DownRight).is(SPACE))
        {
            moveAndTrigger(DownRight, Down);
            isMoving = true;
        }
        else if (t.is(LEFT_DEFLECTOR) && grid.find(self,Left).is(SPACE) &&
            grid.find(self,DownLeft).is(SPACE))
        {
            moveAndTrigger(DownLeft, Down);
            isMoving = true;
        }
        else if (t.is(RIGHT_DEFLECTOR) && grid.find(self,Right).is(SPACE) &&
            grid.find(self,DownRight).is(SPACE))
        {
            moveAndTrigger(DownRight, Down);
            isMoving = true;
        }
        else
        {
            isMoving = false;
            return false;
        }
        // If the boulder is moving, let it continue
        level.enqueue(self, Down);
        return true;
    }

// ----------------------------------------------------------------------------
    // A Left Arrow or RightArrow moves in the given direction, slides up or
    // down if there is a deflector or boulder, and kills the player or a
    // monster if it is already moving.

    // Test whether an arrow can go in the given direction (left or right)

    function arrowViable(d)
    {
        var target = grid.find(self, d);
        if (target.is(SPACE, BOMB, MONSTER, BALLOON)) return true;
        return (isMoving && target.is(PLAYER));
    }

    function leftArrowAct(direction)
    {
        if (direction != Left) return false;
        var t = grid.find(self, direction);
        var upDiag = direction == Left ? UpLeft : UpRight;
        var downDiag = direction == Left ? DownLeft : DownRight;

        if (arrowViable(direction))
        {
            moveAndTrigger(direction, direction);
            isMoving = true;
        }
        else if (t.is(BOULDER) && grid.find(self,Up).is(SPACE) &&
                 grid.find(self,upDiag).is(SPACE))
        {
            moveAndTrigger(upDiag, direction);
            isMoving = true;
        }
        else if (t.is(BOULDER) && grid.find(self,Down).is(SPACE) &&
                 grid.find(self,downDiag).is(SPACE))
        {
            moveAndTrigger(downDiag, direction);
            isMoving = true;
        }
        else if (t.is(LEFT_DEFLECTOR) && grid.find(self,Down).is(SPACE) &&
                 grid.find(self,downDiag).is(SPACE,BALLOON))
        {
            moveAndTrigger(downDiag, Down);
        }
        else if (t.is(RIGHT_DEFLECTOR) && grid.find(self,Up).is(SPACE) &&
                 grid.find(self,upDiag).is(SPACE,BALLOON))
        {
            moveAndTrigger(upDiag, Up);
        }
        else
        {
            isMoving = false;
            return false;
        }
        // If the arrow is moving, let it continue
        level.enqueue(self, direction);
        return true;
    }

    function rightArrowAct(direction)
    {
        if (direction != Right) return false;
        var t = grid.find(self, direction);
        var upDiag = direction == Left ? UpLeft : UpRight;
        var downDiag = direction == Left ? DownLeft : DownRight;
        if (arrowViable(direction))
        {
            moveAndTrigger(direction, direction);
            isMoving = true;
        }
        else if (t.is(BOULDER) && grid.find(self,Up).is(SPACE) &&
                 grid.find(self,upDiag).is(SPACE))
        {
            moveAndTrigger(upDiag, direction);
            isMoving = true;
        }
        else if (t.is(BOULDER) && grid.find(self,Down).is(SPACE) &&
                 grid.find(self,downDiag).is(SPACE))
        {
            moveAndTrigger(downDiag, direction);
            isMoving = true;
        }
        else if (t.is(RIGHT_DEFLECTOR) && grid.find(self,Down).is(SPACE) &&
                 grid.find(self,downDiag).is(SPACE,BALLOON))
        {
            moveAndTrigger(downDiag, Down);
        }
        else if (t.is(LEFT_DEFLECTOR) && grid.find(self,Up).is(SPACE) &&
                 grid.find(self,upDiag).is(SPACE,BALLOON))
        {
            moveAndTrigger(upDiag, Up);
        }
        else
        {
            isMoving = false;
            return false;
        }
        // If the arrow is moving, let it continue
        level.enqueue(self, direction);
        return true;
    }

// ----------------------------------------------------------------------------
    // A balloon rises, sliding left or right if there is a deflector or
    // another balloon above.  It is relatively harmless and doesn't kill, but
    // can be popped by an arrow.

    function balloonAct(dir)
    {
        if (dir != Up) return false;
        var t = grid.find(self, Up);
        if (t.is(SPACE))
        {
            moveAndTrigger(Up, Up);
        }
        else if (t.is(BOULDER))
        {
            if (grid.find(self,Left).is(SPACE) &&
                grid.find(self,UpLeft).is(SPACE))
            {
                moveAndTrigger(UpLeft, Up);
            }
            else if (grid.find(self,Right).is(SPACE) &&
                     grid.find(self,UpRight).is(SPACE))
            {
                moveAndTrigger(UpRight, Up);
            }
            else return false;
        }
        else if (t.is(LEFT_DEFLECTOR))
        {
            if (! grid.find(self,Right).is(SPACE)) return false;
            if (! grid.find(self,UpRight).is(SPACE)) return false;
            moveAndTrigger(UpRight, Right);
        }
        else if (t.is(RIGHT_DEFLECTOR))
        {
            if (! grid.find(self,Left).is(SPACE)) return false;
            if (! grid.find(self,UpLeft).is(SPACE)) return false;
            moveAndTrigger(UpLeft, Left);
        }
        else return false;
        level.enqueue(self, Up);
        return true;
    }

    function balloonCollide(arrow)
    {
        grid.replace(self,arrow);
    }

// ----------------------------------------------------------------------------
    // A teleport moves the player to the arrival point, and triggers both the
    // space left behind and five cells round the arrival point.

    function teleportCollide(player)
    {
        level.addScore(20);
        var pos = level.getArrival();
        var target = grid.get(pos.x, pos.y);
        level.enqueue(target, UpLeft);
        level.enqueue(target, DownLeft);
        level.enqueue(target, DownRight);
        level.enqueue(target, UpRight);
        level.enqueue(target, Here);
        level.enqueue(self, Here);
        grid.replace(self, space());
        grid.replace(target, player);
    }
// ----------------------------------------------------------------------------
    // This is the arrival point of the teleport.  After reporting its position
    // as a global, it replaces itself with a space.

    function arrivalInit()
    {
        level.setArrival(self);
        grid.replace(self, space());
    }
// ----------------------------------------------------------------------------
    // The time capsule allows the player extra moves.

    function timeCapsuleCollide(player)
    {
        level.addScore(5);
        level.addMoves(+250);
        grid.replace(self, player);
    }

// ----------------------------------------------------------------------------
    // The Monster chases and tries to eat the player.  It moves in step with
    // the player, and moves either vertically or horizontally toward the
    // player, according both to which distance is greater, and which direction
    // is available to it.  The monster can be killed by a boulder or an arrow.

    function monsterInit()
    {
        level.setMonster(self);
    }

    function monsterViable(d)
    {
        var target = grid.find(self, d);
        return target.is(SPACE, PLAYER);
    }

    function monsterAct(dir)
    {
        var horizontal = Right;
        var vertical = Down;
        var dx = grid.distance(self, level.getPlayer(), true);
        var dy = grid.distance(self, level.getPlayer(), false);
        if (dx < 0) { horizontal = Left; dx = -dx; }
        if (dy < 0) { vertical = Up; dy = -dy; }
        if (dx > dy && monsterViable(horizontal)) move(self, horizontal);
        else
        {
            if (monsterViable(vertical)) move(self, vertical);
            else if (monsterViable(horizontal)) move(self, horizontal);
            else return false;
        }
        return true;
    }

    function monsterCollide(other)
    {
        grid.replace(self, other);
        level.setMonster(null);
        level.addScore(100);
    }

// ----------------------------------------------------------------------------
    // A baby monster does left wall following, and ends up trapped by a cage.
    //
    // It can end up on top of another entity, specifically space, earth, a
    // boulder, an arrow or a balloon.  Any number of baby monsters can end up
    // on top of each other in theory (and in practice, there is at least one
    // situation in one level where three end up on top of each other).  In
    // addition, a baby monster can sometimes be invisible, i.e. off the grid,
    // as part of its natural behaviour cycle, or because it is underneath
    // another baby monster.  Almost always, the baby eventually gets caught in
    // a cage, and turns into treasure.
    //
    // The behaviour of babies is as follows.  The player moves (and perhaps
    // the big monster), and all the ensuing triggered events happen.  Moving
    // entities (boulders, arrows, balloons) stop if they hit a baby.  After
    // that, each baby gets a turn to move.  It uses a left wall following
    // algorithm (i.e. it moves as if keeping its left appendage against the
    // wall) and it can walk over earth without eating it.  When it has moved,
    // it becomes temporarily invisible, and triggers nearby entities.  While
    // the triggered events happen, moving objects completely ignore invisible
    // babies, but are stopped by visible ones.  After that, the baby makes
    // itself visible again (which may leave it on top of a moving object which
    // happens to have stopped in the baby's grid cell).
    //
    // In the original, babies move even if they are trapped in a single cell
    // and they have to move on top of something.  That behaviour is reproduced
    // here, except that they don't move on top of a wall, so they can't escape
    // from the playing area (as in the original level 41).
    //
    // The baby has a current direction, its most recent position (if it
    // is off the grid) and the entity (space, earth, boulder or
    // arrow) which it is currently covering up (if it is on the grid).

    function babyViable(dir)
    {
        var target = grid.find(self, dir);
        return target.is(SPACE, EARTH, PLAYER, CAGE, BABY_MONSTER);
    }

    function babyLeft()
    {
        switch (babyDirection)
        {
        case Up: return Left;
        case Down: return Right;
        case Left: return Down;
        case Right: return Up;
        default: return null;
        }
    }

    function babyHide()
    {
        if (grid.nowhere(self)) return;
        babyRecent = grid.position(self);
        grid.replace(self, babyUnder);
        babyUnder = null;
    }

    function babyShow()
    {
        if (! grid.nowhere(self)) return;
        var entity = grid.get(babyRecent.x, babyRecent.y);
        if (entity.is(BABY_MONSTER))
        {
            entity.babyHide();
            entity = grid.get(babyRecent.x, babyRecent.y);
        }
        babyUnder = entity;
        grid.replace(entity, self);
    }

    function babyMove(dir)
    {
        var target = grid.find(self, dir);
        babyHide();
        babyRecent = grid.position(target);
        if (target.is(PLAYER,CAGE)) { target.collide(self); return; }
        babyShow();
        display.sleep();
    }

    function babyInit()
    {
        babyUnder = space();
        babyDirection = Right;
        if (! babyViable(Up)) babyDirection = Right;
        else if(! babyViable(Right)) babyDirection = Down;
        else if(! babyViable(Down)) babyDirection = Left;
        else if(! babyViable(Left)) babyDirection = Up;
        level.addBaby(self);
    }

    function babyAct(dir)
    {
        if (grid.nowhere(self)) babyShow();
        var ahead = babyDirection;
        var left = babyLeft();
        var right = left.opposite;
        var back = babyDirection.opposite;
        if (babyViable(left)) babyDirection = left;
        else if (babyViable(ahead)) babyDirection = ahead;
        else if (babyViable(right)) babyDirection = right;
        else if (! grid.find(self,back).is(WALL)) babyDirection = back;
        else return false;
        start(babyDirection);
        babyMove(babyDirection);
//        moveAndTrigger(babyDirection, babyDirection);
        babyHide();
        return true;
    }

    function babyCollide(other)
    {
        babyRecent = grid.position(self);
        grid.replace(self, other);
        if (other.is(PLAYER)) other.collide(self);
    }
}
