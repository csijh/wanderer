package model;
import java.util.*;
import java.lang.reflect.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This is the current global state of a game.  The state holds collections of
entities, strings and counters, accessed using variables.  The defaults are null
entities, empty strings, and zero counters.

This class is generic to avoid cyclic dependencies, and to allow for a
game-specific Entity class. */

class State<E> {
    private Map<String,E> entities = new HashMap<>();
    private Map<String,String> strings = new HashMap<>();
    private Map<String,Integer> counters = new HashMap<>();

    // Clear the state ready for a new level.
    void reset() {
        entities.clear();
        strings.clear();
        counters.clear();
    }

    // Set a named entity, string or counter.
    void set(String v, E e) { entities.put(v, e); }
    void set(String v, int n) { counters.put(v, n); }
    void set(String v, String s) { strings.put(v, s); }

    // Get a named entity.
    E entity(String v) {
        return entities.get(v);
    }

    // Get a named string.
    String string(String v) {
        String s = strings.get(v);
        return (s == null) ? "" : s;
    }

    // Get a named counter.
    int count(String v) {
        Integer i = counters.get(v);
        return (i == null) ? 0 : i;
    }

    // Add to a named counter.
    void add(String v, int n) {
        set(v, count(v) + n);
    }

    // Testing
    private static void claim(boolean b) { if (! b) throw new Error("Bug"); }
    private static class Item {}
    public static void main(String[] args) {
        Item p = new Item();
        State<Item> state = new State<>();
        state.set("PLAYER", p);
        claim(state.entity("PLAYER") == p);
        state.set("TITLE", "title");
        claim(state.string("TITLE").equals("title"));
        claim(state.count("SCORE") == 0);
        state.set("SCORE", 2);
        claim(state.count("SCORE") == 2);
        System.out.println("State class OK");
    }
}
