package view;
import javafx.beans.property.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

A high score for a level, to be used as a table row. */

public class Score {
    final int index;
    Score(int i) { index = i; }
    private final StringProperty level = new SimpleStringProperty();
    private final BooleanProperty done = new SimpleBooleanProperty();
    private final IntegerProperty score = new SimpleIntegerProperty();
    public StringProperty levelProperty() { return level; }
    public BooleanProperty doneProperty() { return done; }
    public IntegerProperty scoreProperty() { return score; }
    public String getLevel() { return level.get(); }
    public boolean getDone() { return done.get(); }
    public int getScore() { return score.get(); }
    public void setLevel(String s) { level.set(s); }
    public void setDone(boolean b) { if (b) done.set(b); }
    public void setScore(int n) { if (n > score.get()) score.set(n); }

    // No testing
    public static void main(String[] args) {
        System.out.println("Score class OK");
    }
}
