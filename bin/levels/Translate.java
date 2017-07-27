import java.io.*;
import java.util.*;

/* Translate a screen file from the original wanderer game into a level file
for the reconstruction of the game.  The first line contains the width and
height (fixed at 42x18) and allowed time (from the last line of the screen
file, with 0 meaning no limit).  The grid has has an extra walled border added
round the edge.  The spaces and the minus signs on the ends of the original
lines are converted into dots. */

class Translate
{
    public static void main(String[] args) throws Exception
    {
        if (args.length != 2) error();
        if (args[0].startsWith("screen")) toLevel(args[0], args[1]);
        else error();
    }

    static void toLevel(String screen, String level) throws Exception
    {
        String[] lines = read(screen);
        PrintWriter out = new PrintWriter(level);
        out.print("42 18 ");
        out.println(lines[17]);
        for (int i=0; i<42; i++) out.print('#');
        out.println();
        for (int i=0; i<16; i++)
        {
            String line = lines[i];
            line = line.replaceAll(" ",".");
            line = line.replace('-','.');
            out.print('#');
            out.print(line);
            out.println('#');
        }
        for (int i=0; i<42; i++) out.print('#');
        out.println();
        out.println(lines[16]);
        out.close();
    }

    static String[] read(String screen) throws Exception
    {
        Scanner in = new Scanner(new File(screen));
        String[] lines = new String[18];
        for (int n=0; n<17; n++) lines[n] = in.nextLine();
        if (in.hasNextLine()) lines[17] = in.nextLine();
        else lines[17] = "0";
        in.close();
        return lines;
    }

    static void error()
    {
        System.err.println("Usage:");
        System.err.println("  java Translate screen-file level-file");
        System.exit(1);
    }
}
