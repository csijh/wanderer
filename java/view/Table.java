package view;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.text.*;
import javafx.scene.paint.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.beans.property.*;
import java.util.*;
import java.io.*;

/* By Ian Holyer, 2017. Free and open source: see licence.txt.

Provide a high score table component for the user interface. */

public class Table extends Stage {
    private String game;
    private String current;
    private int index;
    private ObservableList<Score> scores;
    private Map<String,Score> map;
    private TableView<Score> view;
    private File file;

    public Table(String g, String[] levels) {
        game = g;
        createScores(levels);
        load();
        Scene table = createScene();
        setScene(table);
        setTitle("High Scores");
    }

    // Allow Stage.show to be triggered by an event.
    public void show(ActionEvent e) { show(); }

    // Create the list and map of Score objects.
    private void createScores(String[] levels) {
        scores = FXCollections.observableArrayList();
        map = new HashMap<>();
        for (int i=0; i<levels.length; i++) {
            Score score = new Score(i);
            score.setLevel(levels[i]);
            scores.add(score);
            map.put(levels[i], score);
        }
    }

    private Scene createScene() {
        view = new TableView<>();
        view.setItems(scores);
        TableColumn<Score,String> levelCol = new TableColumn<>("Level");
        TableColumn<Score,Boolean> doneCol = new TableColumn<>("Done");
        TableColumn<Score,Integer> scoreCol = new TableColumn<>("Score");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        doneCol.setCellValueFactory(new PropertyValueFactory<>("done"));
        doneCol.setCellFactory(tc -> new CheckBoxTableCell<>());
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        view.getColumns().add(levelCol);
        view.getColumns().add(doneCol);
        view.getColumns().add(scoreCol);
        ScrollPane sp = new ScrollPane();
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setContent(view);
        return new Scene(sp);
    }

    // Get the current level.
    public String current() {
        return current;
    }

    // Set the current level.
    public void current(String level) {
        current = level;
        view.getSelectionModel().clearAndSelect(map.get(current).index);
    }

    // Report a score for a level.
    void score(String level, int n) {
        Score score = map.get(level);
        score.setScore(n);
    }

    // Report success for a level.
    void success(String level, boolean b) {
        Score score = map.get(level);
        score.setDone(b);
    }

    public void close() { store(); }

    private void load() {
        File home = new File(System.getProperty("user.home"));
        File folder = new File(home, "." + game.toLowerCase());
        folder.mkdir();
        file = new File(folder, "scores.txt");
        try { file.createNewFile(); }
        catch (Exception e) { throw new Error(e); }
        Scanner in = null;
        try { in = new Scanner(file); }
        catch (Exception e) { throw new Error(e); }
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] entry = line.split(" ");
            String level = entry[0];
            Score score = map.get(level);
            if (entry[1].equals("1")) current = level;
            if (entry[2].equals("1")) score.setDone(true);
            score.setScore(Integer.parseInt(entry[3]));
        }
        in.close();
    }

    private void store() {
        PrintWriter out = null;
        try { out = new PrintWriter(file); }
        catch (Exception e) { throw new Error(e); }
        for (Score score : scores) {
            out.print(score.getLevel() + " ");
            out.print(score.getLevel().equals(current) ? "1 " : "0 ");
            out.print(score.getDone() ? "1 " : "0 ");
            out.println(score.getScore());
        }
        out.close();
    }

    // Launch table on its own for manual testing.
    public static class TestTable extends Application {
        public void start(Stage stage) {
            String[] levels = {"1", "2"};
            Table table = new Table("Game", levels);
            table.show();
        }
    }

    // Launch the table on its own, for manual testing.
    public static void main(String[] args) {
        Application.launch(TestTable.class, args);
    }
}
