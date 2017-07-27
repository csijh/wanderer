package model;
import java.util.*;
import java.lang.reflect.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

This is the current global state of a game.  The state holds collections of
entities, strings and counters, accessed using variables.  The defaults are null
entities, empty strings, and zero counters. The state also provides sample
entities so that entities can create other entities.

This class is generic to avoid cyclic dependencies, and to allow for a
game-specific Entity class. */

class State<E> {
    private Map<Variable,E> entities = new HashMap<>();
    private Map<Variable,String> strings = new HashMap<>();
    private Map<Variable,Integer> counters = new HashMap<>();
    private Map<Character,E> samples;

    State(Map<Character,E> s) { samples = s; }

    // Set a named entity, string or counter.
    void set(Variable v, E e) { entities.put(v, e); }
    void set(Variable v, int n) { counters.put(v, n); }
    void set(Variable v, String s) { strings.put(v, s); }

    // Get a named entity.
    E entity(Variable v) {
        return entities.get(v);
    }

    // Get a named string.
    String string(Variable v) {
        String s = strings.get(v);
        return (s == null) ? "" : s;
    }

    // Get a named counter.
    int count(Variable v) {
        Integer i = counters.get(v);
        return (i == null) ? 0 : i;
    }

    // Get a sample entity for a given character code.
    E sample(char c) { return samples.get(c); }

    // Testing
    private static void claim(boolean b) { if (! b) throw new Error("Bug"); }
    private static class Item {}
    public static void main(String[] args) {
        Item sample = new Item();
        Item p = new Item();
        Map<Character,Item> samples = new HashMap<>();
        samples.put('i', sample);
        State<Item> state = new State<>(samples);
        state.set(Variable.PLAYER, p);
        claim(state.entity(Variable.PLAYER) == p);
        state.set(Variable.TITLE, "title");
        claim(state.string(Variable.TITLE).equals("title"));
        claim(state.count(Variable.SCORE) == 0);
        state.set(Variable.SCORE, 2);
        claim(state.count(Variable.SCORE) == 2);
        claim(state.sample('i') == sample);
        System.out.println("State class OK");
    }
}
