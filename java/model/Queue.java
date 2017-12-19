package model;
import java.util.*;

/* Queue class. Free and open source: see licence.txt.

A Queue object determines in what order entities act.  An entity can join the
queue at the end, or push to the front. A round begins when a user command is
given, and consists of a number of steps. At each step, next() is called to get
an entity from the queue so that it can act.  When the knock-on effects from one
user command have finished, next() returns null and the round ends.

Entities can declare themselves to be agents, at the start or end of the agents
list, meaning that they get a chance to act autonomously on every round.

This class is generic to avoid cyclic dependencies, and to allow for a
game-specific Entity base class. */

class Queue<E> {
    private char command;
    private Deque<E> actors = new ArrayDeque<E>();
    private Deque<E> agents = new ArrayDeque<E>();

    // Clear the queue, ready for a new level.
    void reset() {
        actors.clear();
        agents.clear();
    }

    // Provide a user command to start a round.  Put the agents onto the queue
    // to give them each a chance to act.
    void command(char cmd) {
        actors.addAll(agents);
        command = cmd;
    }

    // Allow an entity, presumably the player, to pick up the current command.
    char command() { return command; }

    // Queue up an entity, at the end or start of the queue.
    void join(E entity, boolean hi) {
        if (hi) actors.addFirst(entity);
        else actors.addLast(entity);
    }

    // Become an autonomous agent, at the end or start of the agents list.
    void agent(E entity, boolean hi) {
        if (hi) agents.addFirst(entity);
        else agents.addLast(entity);
    }

    // Take an entity from the front of the queue, or return null.
    E next() { return actors.pollFirst(); }

    // Remove a dying or sleeping entity from the queue and agent list.
    void stop(E entity) {
        agents.remove(entity);
        actors.remove(entity);
    }

    // End the game.
    void end() { actors.clear(); agents.clear(); }

    // Testing
    private static void claim(boolean b) { if (! b) throw new Error("Bug"); }
    public static void main(String[] args) {
        Queue<String> queue = new Queue<String>();
        String a = "a", b = "b", c = "c";
        queue.agent(a, false);
        queue.agent(b, false);
        queue.command('^');
        claim(queue.command() == '^');
        claim(queue.next() == a);
        claim(queue.next() == b);
        claim(queue.next() == null);
        queue.command('v');
        queue.join(c, true);
        claim(queue.next() == c);
        claim(queue.next() == a);
        claim(queue.next() == b);
        claim(queue.next() == null);
        queue.command('>');
        claim(queue.next() == a);
        claim(queue.next() == b);
        claim(queue.next() == null);
        System.out.println("Queue class OK");
    }
}
