package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    private List<Word> dictionary;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dictionary = this.insertFromFile();
        initListView(dictionary);
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

    public List<Word> insertFromFile() {
        List<Word> words = new ArrayList<>();
        try {
            FileReader fr = new FileReader("dictionary.txt", StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(fr);
            String s;
            while ((s = br.readLine()) != null) {
                String[] arr = s.split("\t");
                Word newWord = new Word(arr[0], arr[1]);
                words.add(newWord);
            }
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }

    private List<String> targetWords(List<Word> list) {
        List<String> result = new ArrayList<>();
        for (Word word : list) {
            result.add(word.getWordTarget());
        }
        return result;
    }

    public void displaySelected(MouseEvent event) {
        Word wordSelected = lvWords.getSelectionModel().getSelectedItem();
        if (wordSelected != null) {
            String text = "Selected word: " + wordSelected.getWordTarget();
            text += "\nWord Explain: " + wordSelected.getWordExplain();
            textArea.setText(text);
        }
    }
}
