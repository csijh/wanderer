package model;
import java.util.*;

/* Grid class. Free and open source: see licence.txt.

Provide a grid for entities to inhabit.  At each (x,y) coordinate, there may be
several entities in front of each other.  If the coordinates of an entity are
kept consistent with its grid location, the grid guarantees that an entity
appears at most once in its cell. The grid has a flag to keep track of whether
anything has changed recently.

An entity can hide, off the grid.  It retains its grid position, but the only
action available is to show itself, i.e. go back on the grid at its old
position.

This class is generic to avoid cyclic dependencies, and to allow for a
game-specific Entity class. */

class Grid<E> {
    private int width, height;
    private boolean changed;
    private Deque<E>[][] cells;

    Grid(int w, int h) {
        reset(w, h);
    }

    // Clear the grid, ready for a new level, not necessarily the same size.
    void reset(int w, int h) {
        width = w;
        height = h;
        changed = false;
        newArray();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new ArrayDeque<E>();
            }
        }
    }

    // Initialise the cells array, bypassing Java's generic problems.
    @SuppressWarnings("unchecked")
    private void newArray() { cells = new Deque[width][height]; }

    // Find the front entity in a cell.
    E front(int x, int y) {
        return cells[x][y].peekFirst();
    }

    // Find the back entity in a cell.
    E back(int x, int y) {
        return cells[x][y].peekLast();
    }

    // Push an off-grid entity onto the front of its cell.
    void show(int x, int y, E e) {
        changed = true;
        if (cells[x][y].contains(e)) throw new Error("Already in grid");
        cells[x][y].addFirst(e);
    }

    // Check if an entity is off the grid.
    boolean hidden(int x, int y, E e) {
        return ! cells[x][y].contains(e);
    }

    // Add an entity at the back of a given cell.
    void back(int x, int y, E e) {
        changed = true;
        if (cells[x][y].contains(e)) throw new Error("Already in grid");
        cells[x][y].addLast(e);
    }

    // Pop the front entity from a cell.
    private E pop(int x, int y) {
        changed = true;
        return cells[x][y].pollFirst();
    }

    // Remove an entity from its cell, so it is not in the grid.
    void hide(int x, int y, E e) {
        changed = true;
        boolean ok = cells[x][y].remove(e);
        if (! ok) throw new Error("Not in the grid");
    }

    // Set the changed flag.
    void changed(boolean b) { changed = b; }

    // Check whether anything has changed since the last time the changed flag
    // was set to false.
    boolean changed() { return changed; }

    // Testing
    private static void claim(boolean b) { if (! b) throw new Error("Bug"); }
    public static void main(String[] args) {
        Grid<String> grid = new Grid<String>(2,2);
        String a = "a", b = "b";
        grid.show(1,1,b);
        grid.show(1,1,a);
        claim(grid.front(1,1) == a);
        claim(grid.back(1,1) == b);
        claim(grid.pop(1,1) == a);
        claim(grid.pop(1,1) == b);
        grid.show(1,1,b);
        grid.show(1,1,a);
        grid.hide(1,1,b);
        claim(grid.pop(1,1) == a);
        claim(grid.pop(1,1) == null);
        grid.changed(false);
        claim(! grid.changed());
        grid.show(1,1,a);
        claim(grid.changed());
        System.out.println("Grid class OK");
    }
}
