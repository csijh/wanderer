package wanderer;
import static wanderer.Entity.*;
import model.*;
import java.io.*;
import java.util.*;

/* Test class. Free and open source: see licence.txt.

The Test class drives non-graphics level testing. */

class Test {

    Entity hatch(char type) {
        switch (type) {
            case Space: return new Space();
            case Wall: case Rock: case Earth: case Star: case Cage: case Time:
            case Landmine: case Arrival: case Teleport: case Exit:
            case LeftDeflector: case RightDeflector: case Dead:
                return new Thing();
            case Boulder: return new Boulder();
            case Balloon: return new Balloon();
            case LeftArrow: return new LeftArrow();
            case RightArrow: return new RightArrow();
            case Monster: return new Monster();
            case Baby: return new Baby();
            case Player: return new Player();
            default: return null;
        }
    }

    String[] testNames = {
        "t1a", "t2a", "t2b", "1a", "1b", "1c", "1d", "2a", "3a", "4a", "5a",
        "6a", "6b", "7a", "8a", "9a", "10a", "10b", "11a", "12a", "13a", "14a",
        "15a", "15b", "16a", "17a", "18a", "19a", "20a", "21a", "22a", "23a",
        "24a", "25a", "26a", "27a", "28a", "29a", "30a", "31a", "32a", "33a",
        "34a", "35a", "36a", "37a", "38a", "39a", "40a", "41a", "42a", "43a",
        "44a", "45a", "46a", "47a", "48a", "49a", "50a", "51a", "52a", "53a",
        "54a", "55a", "56a", "57a", "58a", "59a", "60a", "60b"
    };

    String[] tests() {
        String[] tests = new String[testNames.length];
        for (int i=0; i<tests.length; i++) {
            tests[i] = "/tests/" + testNames[i] + ".txt";
        }
        return tests;
    }

    String[] levels() {
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
        Test t = new Test();
        Level<Entity> level = new Level<>(t::hatch);
        int count = level.tests(t.tests(), t.levels());
        System.out.println("Test class OK: " + count + " tests succeeded");
    }
}
