package model;

/* Hatchery interface. Free and open source: see licence.txt.

This defines a factory object for creating entities.  To avoid a direct
dependency on entity classes, it is a generic interface.

Since this is a single-method interface, in Java 8 onwards, a function can be
used to implement it. */

public interface Hatchery<E> {
    // Construct a fresh uninitialised entity of a given type.
    E hatch(char type);

    public static void main(String[] args) {
        System.out.println("Hatchery interface OK");
    }
}
