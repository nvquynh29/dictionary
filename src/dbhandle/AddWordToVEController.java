package dbhandle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.Controller;
import sample.DatabaseConnection;
import sample.Word;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AddWordToVEController {
    @FXML
    Label lblEnglish;

    @FXML
    Label lblVietnamese;

    @FXML
    Label lblDescription;

    @FXML
    TextField txtEnglish;

    @FXML
    TextField txtVietnamese;

    @FXML
    TextArea txtDescription;

    @FXML
    Button btnAdd;

    public Word newWord;

    public void addNewWord(ActionEvent event) {
        if (txtEnglish.getText().trim().isEmpty() || txtVietnamese.getText().trim().isEmpty()) {
            showAlert("Enter word in English and Vietnamese!");
        } else {
            String word, html;
            String description = "";
            description = txtDescription.getText();
            word = txtVietnamese.getText();
            String explain = txtEnglish.getText();
            html = AddWordToEVController.wordToHtml(word, explain, description, "");
            newWord = new Word(0, word, html, description, "");
            if (newWord != null) {
                boolean isExisted = false;
                try {
                    ResultSet rs = DatabaseConnection.getResultSet("va");
                    while (rs.next()) {
                        String wordTarget = rs.getString("word");
                        if (wordTarget.equals(word)) {
                            isExisted = true;
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (isExisted == false) {
                    showAlert("Success!");
                    sample.DatabaseConnection.addWordToDB("va", newWord);
                    Controller.setDictionaryVE();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText(word);
                    alert.setContentText("This word has existed!\n Do you want to update it?");
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    ButtonType buttonAccept = new ButtonType("Confirm");
                    alert.getButtonTypes().setAll(buttonAccept, buttonTypeCancel);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonAccept) {
                        DatabaseConnection.updateWordToDB("va", word, html, description, "");
                        Controller.setDictionaryVE();
                    }
                }
            }
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
