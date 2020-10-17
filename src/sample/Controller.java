package sample;

import dbhandle.AlertController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

    private static List<Word> dictionaryEV;
    private static List<Word> dictionaryVE;
    private static Stack<Word> stackSearched;

    Connection conn = DatabaseConnection.ConnectDB();

    public static void setDictionaryEV() {
        dictionaryEV = DatabaseConnection.getDictionary("av");
    }

    public static void setDictionaryVE() {
        dictionaryVE = DatabaseConnection.getDictionary("va");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       initComponent();
    }

    // Initialize all component
    public void initComponent() {
        dictionaryEV = DatabaseConnection.getDictionary("av");
        dictionaryVE = DatabaseConnection.getDictionary("va");
        initListView(dictionaryEV);
        initButton();
        initComboBox();
        stackSearched = new Stack<>();
    }
    public void initListView(List<Word> words) {
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


//    public static List<Word> getDictionary(String tableName) {
//
//        List<Word> result = new ArrayList<>();
//
//        if (DatabaseConnection.isConnected()) {
//            try {
//                ResultSet rs = DatabaseConnection.getResultSet(tableName);
//                while (rs.next()) {
//                    int id = rs.getInt("id");
//                    String word = rs.getString("word");
//                    String html = rs.getString("html");
//                    String description = rs.getString("description");
//                    String pronounce = rs.getString("pronounce");
//
//                    Word newWord = new Word(id, word, html, description, pronounce);
//                    result.add(newWord);
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("Error while load database!");
//            System.exit(1);
//        }
//        return result;
//    }

    public void displaySelected() {
        Word wordSelected = lvWords.getSelectionModel().getSelectedItem();
        txtSearch.setText(wordSelected.getWordTarget());
        if (wordSelected != null) {
            WebEngine webEngine = webView.getEngine();
            String html = wordSelected.getHtml();
            webEngine.loadContent(html);

            //update Stack wordSelected and add condition to enable btnBack
            if (stackSearched.size() == 0 || wordSelected != stackSearched.lastElement()) {
                stackSearched.push(wordSelected);
            }
            if (stackSearched.size() > 1) {
                btnBack.setDisable(false);
            }
        }
    }

    public void searchWord() {
        String keyWord = txtSearch.getText().trim();
        ArrayList<Word> result = new ArrayList<>();
        int mode = cbLanguage.getSelectionModel().getSelectedIndex();
        if (mode == 0) {
            for (Word temp : dictionaryEV) {
                String thisWord = temp.getWordTarget();
                if (thisWord.toLowerCase().startsWith(keyWord.toLowerCase())) {
                    result.add(temp);
                }
            }
        } else {
            for (Word temp : dictionaryVE) {
                String thisWord = temp.getWordTarget();
                if (thisWord.toLowerCase().startsWith(keyWord.toLowerCase())) {
                    result.add(temp);
                }
            }
        }
        initListView(result);
    }

    public boolean checkSearchBar() {
        String word = txtSearch.getText().trim();
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

    public static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void showAlertInformation(String message) {
        int mode = cbLanguage.getSelectionModel().getSelectedIndex();
        if (mode == 0) {
            if (empty(message)) {
                AlertController.showInfoAlert("Thông Báo!", null,
                        "Vui lòng nhập từ cần tra!");
            }
            else {
                AlertController.showInfoAlert("Translate!", message.trim(),
                        "Meaning");
            }
        }
        else {
            if (empty(message)) {
                AlertController.showInfoAlert("Alert!", null,
                        "Please enter the word!");
            }
            else {
                AlertController.showInfoAlert("Translate!", message.trim(),
                        "Meaning");
            }
        }
    }

    private void showAlertConfirmation() {
        int mode = cbLanguage.getSelectionModel().getSelectedIndex();
        if (mode == 0) {
            AlertController.showConfirmAlert("Confirmation Dialog", txtSearch.getText().trim(),
                    "Từ này không có trong từ điển.\n" + "Chọn option.");
        }
        else {
            AlertController.showConfirmAlert("Confirmation Dialog", txtSearch.getText().trim(),
                    "This word isn't in dictionary.\n" + "Choose your option.");
        }
        Alert alert = AlertController.getAlertConfirm();
        ButtonType buttonTypeOne = new ButtonType("Translate");
        ButtonType buttonTypeTwo = new ButtonType("Add to dictionary");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            showAlertInformation(txtSearch.getText());
        }
        else if (result.get() == buttonTypeTwo) {
            int index = cbLanguage.getSelectionModel().getSelectedIndex();
            if (index == 0) {
                addWordEV();
            } else {
                addWordVE();
            }
        }
    }

    public void OnEnter() {
        if (empty(txtSearch.getText())) {
            showAlertInformation(txtSearch.getText());
        }
        else if (checkSearchBar()) {
            lvWords.getSelectionModel().select(0);
            displaySelected();
        }
        else showAlertConfirmation();
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
    public void backButtonHandle(MouseEvent event) {
        //voi size = 1 tuc la trong stack chinh la tu vua chon nen ko co tu qua khu
        if (stackSearched.size() > 1) {
            //update Stack wordSelected
            stackSearched.pop();
            WebEngine webEngine = webView.getEngine();
            Word lastWord = stackSearched.lastElement();
            lvWords.getSelectionModel().select(lastWord);
            String html = lastWord.getHtml();
            webEngine.loadContent(html);
        }
        if (stackSearched.size() == 1) {
            btnBack.setDisable(true);
        }
    }

    public void addWordEV() {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/dbhandle/AddWordToEV.fxml"));
            Scene addScene = new Scene(root);
            newStage.setScene(addScene);
            newStage.setTitle("Thêm từ mới");
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDictionaryEV();
        initListView(dictionaryEV);
    }

    public void addWordVE() {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/dbhandle/AddWordToVE.fxml"));
            Scene addScene = new Scene(root);
            newStage.setScene(addScene);
            newStage.setTitle("Adding new word");
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDictionaryVE();
        initListView(dictionaryVE);
    }

    public void deleteWordEV() {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/dbhandle/DeleteWordEV.fxml"));
            Scene addScene = new Scene(root);
            newStage.setScene(addScene);
            newStage.setTitle("Xóa từ!");
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDictionaryEV();
        initListView(dictionaryEV);
    }

    public void deleteWordVE() {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/dbhandle/DeleteWordVE.fxml"));
            Scene addScene = new Scene(root);
            newStage.setScene(addScene);
            newStage.setTitle("Deleting word");
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDictionaryVE();
        initListView(dictionaryVE);
    }

    public void updateWordEV() {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/dbhandle/UpdateWordToEV.fxml"));
            Scene addScene = new Scene(root);
            newStage.setScene(addScene);
            newStage.setTitle("Cập nhật từ điển");
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDictionaryEV();
        initListView(dictionaryEV);
    }

    public void updateWordVE() {
        try {
            Stage newStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/dbhandle/UpdateWordToVE.fxml"));
            Scene addScene = new Scene(root);
            newStage.setScene(addScene);
            newStage.setTitle("Update dictionary");
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDictionaryVE();
        initListView(dictionaryVE);
    }

    //Text to Speech
    public void speak(ActionEvent event) {
        String text = txtSearch.getText().trim();
        Speaker speaker = new Speaker();
        speaker.say(text);
    }

    //Google Translate API free v2

    //Text OCR
    public void getTextFromImage(String language) {
        OCRHandleController ocr = new OCRHandleController();
        String text = ocr.getTextOCR(language);
        Stage stage = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("OCRHandle.fxml"));
            Scene scene = new Scene(root);
            Node node = scene.lookup("#textArea");
            TextArea textArea = (TextArea) node;
            textArea.setText(text);

            stage.setTitle("Text Preview");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getTextEnglish() {
        getTextFromImage("eng");
    }

    public void getTextVietnamese() {
        getTextFromImage("vie");
    }
}
