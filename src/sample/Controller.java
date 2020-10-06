package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    Button btnBack;

    @FXML
    Button btnSpeaker;

    @FXML
    Button btnVoice;

    @FXML
    TextField txtSearch;

    @FXML
    ListView<Word> lvWords;

    @FXML
    ComboBox<String> cbLanguage;

    @FXML
    WebView webView;

    private List<Word> dictionaryEV;
    private List<Word> dictionaryVE;
    private Stack<Word> listSearched;

    Connection conn = DatabaseConnection.ConnectDB();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       initComponent();
    }

    // Initialize all component
    public void initComponent() {
        dictionaryEV = getDictionary("av");
        dictionaryVE = getDictionary("va");
        initListView(dictionaryEV);
        initButton();
        initComboBox();

        listSearched = new Stack<>();
    }
    private void initListView(List<Word> words) {
        ObservableList<Word> listWords = FXCollections.observableArrayList();
        listWords.addAll(words);
        lvWords.getItems().clear();
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

    public void initButton() {
        Image imgSpeaker = new Image("icons/speaker.png");
        Image imgVoice = new Image("icons/microphone.png");
        ImageView ivSpeaker = new ImageView(imgSpeaker);
        ImageView ivVoice = new ImageView(imgVoice);

        ivSpeaker.setPreserveRatio(true);
        ivVoice.setPreserveRatio(true);

        btnSpeaker.setGraphic(ivSpeaker);
        btnVoice.setGraphic(ivVoice);

        btnBack.setDisable(true);
    }

    public void initComboBox() {
        String mode1 = "English - Vietnamese";
        String mode2 = "Vietnamese - English";
        List<String> list = new ArrayList<>();
        list.add(mode1);
        list.add(mode2);
        ObservableList<String> oList = FXCollections.observableList(list);
        cbLanguage.setItems(oList);
        cbLanguage.getSelectionModel().select(0);
    }


    public List<Word> getDictionary(String tableName) {

        List<Word> result = new ArrayList<>();

        if (DatabaseConnection.isConnected()) {
            try {
                ResultSet rs = DatabaseConnection.getResultSet(tableName);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String word = rs.getString("word");
                    String html = rs.getString("html");
                    String description = rs.getString("description");
                    String pronounce = rs.getString("pronounce");

                    Word newWord = new Word(id, word, html, description, pronounce);
                    result.add(newWord);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error while load database!");
            System.exit(1);
        }
        return result;
    }

    public void displaySelected() {
        Word wordSelected = lvWords.getSelectionModel().getSelectedItem();
        if (wordSelected != null) {
            WebEngine webEngine = webView.getEngine();
            String html = wordSelected.getHtml();
            webEngine.loadContent(html);

            //update Stack wordSelected and add condition to enable btnBack
            if (listSearched.size() == 0 || wordSelected != listSearched.lastElement()) {
                listSearched.push(wordSelected);
            }
            if (listSearched.size() > 1) {
                btnBack.setDisable(false);
            }
        }
    }

    public void KeyPress() {
        String word = txtSearch.getText();
        ArrayList<Word> result = new ArrayList<>();
        int mode = cbLanguage.getSelectionModel().getSelectedIndex();
        if (mode == 0) {
            for (Word temp : dictionaryEV) {
                String thisWord = temp.getWordTarget();
                if (thisWord.toLowerCase().startsWith(word.toLowerCase())) {
                    result.add(temp);
                }
            }
        } else {
            for (Word temp : dictionaryVE) {
                String thisWord = temp.getWordTarget();
                if (thisWord.toLowerCase().startsWith(word.toLowerCase())) {
                    result.add(temp);
                }
            }
        }
        initListView(result);
    }

    public boolean checkSearchBar() {
        String word = txtSearch.getText();
        ArrayList<Word> result = new ArrayList<>();
        int mode = cbLanguage.getSelectionModel().getSelectedIndex();
        if (mode == 0) {
            for (Word temp : dictionaryEV) {
                String thisWord = temp.getWordTarget();
                if (thisWord.toLowerCase().startsWith(word.toLowerCase())) {
                    result.add(temp);
                }
            }
        } else {
            for (Word temp : dictionaryVE) {
                String thisWord = temp.getWordTarget();
                if (thisWord.toLowerCase().startsWith(word.toLowerCase())) {
                    result.add(temp);
                }
            }
        }
        if (result.size() == 0) return false;
        else return true;
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        int mode = cbLanguage.getSelectionModel().getSelectedIndex();
        if (mode == 0) {
            alert.setTitle("Alert!");
            if (txtSearch.getText().trim().isEmpty() || txtSearch.getText() == null) {
                alert.setHeaderText(null);
                alert.setContentText("Please enter the word!");
            }
            else if (!checkSearchBar()) {
                alert.setHeaderText(txtSearch.getText());
                alert.setContentText("Do you want to search online?");
            }
        }
        else {
            alert.setTitle("Thông Báo!");
            if (txtSearch.getText().trim().isEmpty() || txtSearch.getText() == null) {
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập từ cần tra!");
            }
            else if (!checkSearchBar()) {
                alert.setHeaderText(txtSearch.getText());
                alert.setContentText("Bạn có muốn tra từ online không?");
            }
        }
        alert.showAndWait();
    }

    public void OnEnter() {
        if (checkSearchBar()) {
            lvWords.getSelectionModel().select(0);
            displaySelected();
        }
        else showAlert();
    }

    public void selectLanguage(ActionEvent event) {
        int index = cbLanguage.getSelectionModel().getSelectedIndex();
        if (index == 0) {
            initListView(dictionaryEV);
        } else {
            initListView(dictionaryVE);
        }
    }

    //handler mouse event for bnBack
    public void backButtonHandler(MouseEvent event) {
        //voi size = 1 tuc la trong stack chinh la tu vua chon nen ko co tu qua khu
        if (listSearched.size() > 1) {
            //update Stack wordSelected
            listSearched.pop();
            WebEngine webEngine = webView.getEngine();
            Word lastWord = listSearched.lastElement();
            lvWords.getSelectionModel().select(lastWord);
            String html = lastWord.getHtml();
            webEngine.loadContent(html);
        }
        if (listSearched.size() == 1) {
            btnBack.setDisable(true);
        }
    }

    public void addEVHandle(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/dbhandle/AddWordToEV.fxml"));
            Scene addScene = new Scene(root);
            newStage.setScene(addScene);
            newStage.setTitle("Thêm từ mới");
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addVEHandle(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/dbhandle/AddWordToVE.fxml"));
            Scene addScene = new Scene(root);
            newStage.setScene(addScene);
            newStage.setTitle("Adding new word");
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
