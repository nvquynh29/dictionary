package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Button btnBack;

    @FXML
    TextField txtSearch;

    @FXML
    ListView<Word> lvWords;

    @FXML
    TextArea textArea;

    private List<Word> words;
    private List<String> wordTargets;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        words = getDictionaryFromDB();
        initListView(words);
    }

    private void initListView(List<Word> words) {
        ObservableList<Word> listWords = FXCollections.observableArrayList();
        listWords.addAll(words);
        lvWords.getItems().addAll(listWords);
        lvWords.setCellFactory(param -> new ListCell<Word>() {
            @Override
            protected void updateItem(Word item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getWordTarget() == null) {
                    setText(null);
                } else {
                    setText(item.getWordTarget());
                }
            }
        });
    }

    public List<Word> getDictionaryFromDB() {
        Connection conn = DatabaseConnection.ConnectDB();
        words = new ArrayList<>();
        wordTargets = new ArrayList<>();

        if (DatabaseConnection.isConnected()) {
            try {
                ResultSet rs = DatabaseConnection.getResultSet();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String word = rs.getString("word");
                    String html = rs.getString("html");
                    String description = rs.getString("description");
                    String pronounce = rs.getString("pronounce");

                    wordTargets.add(word);
                    Word newWord = new Word(id, word, html, description, pronounce);
                    words.add(newWord);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error while load database!");
            System.exit(1);
        }
        return words;
    }

    public void displaySelected(MouseEvent event) {
        Word wordSelected = lvWords.getSelectionModel().getSelectedItem();
        if (wordSelected != null) {
            String text = "Selected = " + wordSelected.getWordTarget();
            textArea.setText(text);
        }
    }

    public void keyPress(KeyEvent event) {
        
    }
}
