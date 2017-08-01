package wanderer;
import static wanderer.Entity.*;
import model.*;
import java.io.*;
import java.util.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

The Test class drives non-graphics level testing. */

class Test {

    static Entity[] samples = {
        new Space(), new Thing(Wall), new Thing(Rock),
        new Thing(Earth), new Thing(Star), new Thing(Cage),
        new Thing(Time), new Thing(Landmine),
        new Thing(Arrival), new Thing(Teleport),
        new Thing(Exit), new Thing(LeftDeflector),
        new Thing(RightDeflector), new Thing(Dead),
        new Boulder(), new Balloon(),
        new LeftArrow(), new RightArrow(),
        new Monster(), new Baby(), new Player()
    };

    static String[] testNames = {
        "t1a", "t2a", "t2b", "1a", "1b", "1c", "1d", "2a", "3a", "4a", "5a",
        "6a", "6b", "7a", "8a", "9a", "10a", "10b", "11a", "12a", "13a", "14a",
        "15a", "15b", "16a", "17a", "18a", "19a", "20a", "21a", "22a", "23a",
        "24a", "25a", "26a", "27a", "28a", "29a", "30a", "31a", "32a", "33a",
        "34a", "35a", "36a", "37a", "38a", "39a", "40a", "41a", "42a", "43a",
        "44a", "45a", "46a", "47a", "48a", "49a", "50a", "51a", "52a", "53a",
        "54a", "55a", "56a", "57a", "58a", "59a", "60a", "60b"
    };

    static String[] tests() {
        String[] tests = new String[testNames.length];
        for (int i=0; i<tests.length; i++) {
            tests[i] = "/tests/" + testNames[i] + ".txt";
        }
        return tests;
    }

    static String[] levels() {
        String[] levels = new String[testNames.length];
        for (int i=0; i<levels.length; i++) {
            int n = testNames[i].length();
            String level = testNames[i].substring(0, n-1);
            levels[i] = "/levels/" + level + ".txt";
        }
        return levels;
    }

    // Do comprehensive replay testing.
    public static void main(String[] args) {
        Level<Entity> level = new Level<>(samples);
        for (Entity e : samples) e.init(level);
        int count = level.tests(tests(), levels());
        System.out.println("Test class OK: " + count + " tests succeeded");
    }
}
