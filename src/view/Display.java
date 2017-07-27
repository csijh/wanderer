package view;
import model.*;
import model.Cell;
import java.util.*;
import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.geometry.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

Display the grid as a canvas, with a status bar underneath. If the canvas size
is smaller than the grid size (e.g. on a mobile device) the viewport is based on
the player's position. It is assumed that the grid has a wall around the edge,
and the viewport is aligned with the edge when the player is one cell away from
it. The viewport is positioned proportionally between those limits. */

public class Display extends BorderPane {
    private Level<?> level;
    private Canvas canvas;
    private Text status;
    private int width, height;
    private int maxWidth, maxHeight;
    private int cellWidth, cellHeight;
    private int fullWidth, fullHeight;
    private Map<Character,Image> images;

    // Create a blank display given sample entities, level, and max canvas size.
    public Display(Level<?> l, int w, int h) {
        level = l;
        maxWidth = w;
        maxHeight = h;
        canvas = new Canvas();
        status = new Text("");
        TextFlow statusBar = new TextFlow(status);
        statusBar.setPadding(new Insets(3,10,3,10));
        images = new HashMap<>();
        Cell<?>[] samples = level.samples();
        for (Cell<?> e : samples) {
            String path = e.image();
            Image img = new Image(path);
            if (img == null) throw new Error("Can't load " + path);
            images.put(e.code(), img);
            int cw = (int) img.getWidth(), ch = (int) img.getHeight();
            if (cellWidth == 0) cellWidth = cw;
            else if (cw != cellWidth) throw new Error("Bad images");
            if (cellHeight == 0) cellHeight = ch;
            else if (ch != cellHeight) throw new Error("Bad images");
        }
        setCenter(canvas);
        setBottom(statusBar);
    }

    // Call this each time a new level has been loaded.
    public void setup() {
        fullWidth = level.width() * cellWidth;
        fullHeight = level.height() * cellHeight;
        width = fullWidth;
        height = fullHeight;
        if (width > maxWidth) width = maxWidth;
        if (height > maxHeight) height = maxHeight;
        canvas.setWidth(width);
        canvas.setHeight(height);
        redraw();
    }

    // Update the status bar, work out where the viewport should be relative to
    // the full grid, based on the player's position, then draw the grid,
    // allowing normal clipping to create the viewport.
    void redraw() {
        Cell player = level.player();
        status.setText(player.status());
        GraphicsContext g = canvas.getGraphicsContext2D();
        int px = player.x();
        int py = player.y();
        int dx = - (fullWidth - width) * (px - 1) / (level.width() - 3);
        int dy = - (fullHeight - height) * (py - 1) / (level.height() - 3);
        for (int x = 0; x < level.width(); x++) {
            for (int y = 0; y < level.height(); y++) {
                Cell e = level.front(x, y);
                Image img = images.get(e.code());
                g.drawImage(img, x*cellWidth + dx, y*cellHeight + dy);
            }
        }
    }

    // No testing
    public static void main(String[] args) {
        System.out.println("Display class OK");
    }
}
