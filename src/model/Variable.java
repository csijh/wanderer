package model;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A variable is essentially just a string used as a name for something stored in
the State class. Some standard variables are defined. NAME and TITLE are strings
filled in by the level, which can be used for generating status messages.  MOVES
is a counter filled in by the level, giving the maximum number of moves allowed
to solve the level. PLAYER is an entity filled in by the entities on loading,
allowing the level to know where the player is. SCORE is a counter and SUCCESS
is a flag (0 or 1 counter), which are updated by entities and used for a high
score table. Further game-specific variables can be defined as desired. */

public class Variable {
    final String name;

    public Variable(String n) { name = n; }

    public static Variable
        NAME = new Variable("NAME"),
        TITLE = new Variable("TITLE"),
        MOVES = new Variable("MOVES"),
        PLAYER = new Variable("PLAYER"),
        SCORE = new Variable("SCORE"),
        SUCCESS = new Variable("SUCCESS");

    public static void main(String[] args) {
        System.out.println("Variable class OK");
    }
}
