package view;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.event.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;

/* Help class. Free and open source: see licence.txt.

This is a utility class which can be used in a javafx user interface to display
help pages. It reads files from a help directory. The files are written in a
custom micro markdown language. The language accepts lines of these forms:

    # title
    [link text](page to link to)
    ! filename of image in images folder
    - line of monospaced text
    ordinary text

A blank line forms a paragraph break. A line consisting of "-" on its own can be
used to generate a gap between paragraphs. */

public class Help extends Stage {
    private int width, height;
    private Stack<String> pageNames;
    private Map<String,Scene> scenes;

    public Help(Stage parent, int w, int h, String index) {
        super(StageStyle.UTILITY);
        initOwner(parent);
        width = w;
        height = h;
        setTitle("Help");
        createPages(index);
        setScene(scenes.get(index));
    }

    // Allow Stage.show to be triggered by an event.
    public void show(ActionEvent e) { show(); }

    // Create a scene per page. As each page is loaded, any other pages which it
    // links to are queued up to be loaded.
    void createPages(String index) {
        pageNames = new Stack<String>();
        scenes = new HashMap<String,Scene>();
        pageNames.push(index);
        while (! pageNames.isEmpty()) {
            String name = pageNames.pop();
            if (scenes.get(name) != null) continue;
            scenes.put(name, loadPage(name));
        }
    }

    // Create a graphical link.
    Hyperlink link(String text, String to) {
        pageNames.push(to);
        Hyperlink h = new Hyperlink(text);
        h.setUserData(to);
        h.setOnAction(this::jump);
        h.setUnderline(true);
        h.setBorder(Border.EMPTY);
        return h;
    }

    // When the user clicks a link, display the appropriate page.
    void jump(ActionEvent e) {
        Node link = (Node) e.getSource();
        String target = (String) link.getUserData();
        setScene(scenes.get(target));
    }

    // Load a page from a markdown file.
    private Scene loadPage(String n) {
        TextFlow page = new TextFlow();
        page.setPadding(new Insets(10));
        List<Node> nodes = page.getChildren();
        InputStream is = getClass().getResourceAsStream("/help/" + n + ".md");
        Reader rdr;
        try { rdr = new InputStreamReader(is); }
        catch(Exception err) { throw new Error("Can't read /help/" +n+ ".md"); }
        Scanner scanner = new Scanner(rdr);
        while (scanner.hasNextLine()) addLine(nodes, scanner.nextLine());
        page.setMaxWidth(width-10);
        ScrollPane sp = new ScrollPane();
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setContent(page);
        sp.setStyle("-fx-background:#ffffff;");
        return new Scene(sp, width, height, Color.WHITE);
    }

    // Add the items generated from one line to the current page.
    private void addLine(List<Node> nodes, String line) {
        if (line.startsWith("#")) addTitle(nodes, line);
        else if (line.startsWith("-")) addList(nodes, line);
        else if (line.startsWith("[")) addLink(nodes, line);
        else if (line.startsWith("!")) addImage(nodes, line);
        else if (line.length() == 0) addBreak(nodes);
        else addText(nodes, line);
    }

    // Add a title to the current page.  There is no newline after it, so that a
    // back link can be added immediately following the title.
    private void addTitle(List<Node> nodes, String line) {
        Text title = new Text(line.substring(2));
        title.setFont(new Font(20));
        nodes.add(title);
    }

    // Add a monospaced line of text, with a newline.
    private void addList(List<Node> nodes, String line) {
        if (line.length() > 2) line = line.substring(2);
        else line = "\n";
        Text text = new Text(line + "\n");
        text.setFont(Font.font("Monospaced", 14));
        nodes.add(text);
    }

    // Add a link to the page.
    private void addLink(List<Node> nodes, String line) {
        line = line.substring(1);
        int close = line.indexOf(']');
        String text = line.substring(0, close);
        String to = line.substring(close+2, line.length() - 1);
        nodes.add(new Text("    "));
        nodes.add(link(text, to));
    }

    // Add an image to the page, with a gap on either side.
    private void addImage(List<Node> nodes, String line) {
        line = line.substring(2);
        Image img = null;
        try { img = new Image(line); }
        catch (Exception err) {
            System.out.println("img " + line);
            System.exit(1);
        }
        ImageView view = new ImageView(img);
        view.setTranslateY(3);
        nodes.add(new Text("    "));
        nodes.add(view);
        nodes.add(new Text("    "));
    }

    // Add a paragraph break to the page.
    private void addBreak(List<Node> nodes) { nodes.add(new Text("\n")); }

    // Add ordinary text to a paragraph.
    private void addText(List<Node> nodes, String line) {
        Text t = new Text(line + " ");
        t.setFont(new Font(14));
        nodes.add(t);
    }

    // Launch the help window on its own for manual testing.
    public static class TestHelp extends Application {
        public void start(Stage stage) {
            new Help(null, 700, 500, "Wanderer").show();
        }
    }
    public static void main(String[] args) {
        Application.launch(TestHelp.class, args);
    }
}
