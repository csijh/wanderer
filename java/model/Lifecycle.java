package model;

/* These are the methods that define the lifecycle of entities. It is expected
that clone and wake will be supplied by the Cell class, and the remainder by
custom game-specific entity classes. */

interface Lifecycle<E> extends Cloneable {
    // Make an exact (shallow) copy of an entity.
    E clone();
    // Wake at a specific place in the grid.
    void wake(int x, int y);
    // Find the character code used in level files for this type of entity.
    char code();
    // Find the file path to the image for this type of entity.
    String image();
    // Make any desired changes at the start of a level.
    void hatch();
    // Make one basic action during play.
    void act();
    // Provide a status string for the user interface.
    String status();

    public static void main(String[] args) {
        System.out.println("Lifecycle interface OK");
    }
}
