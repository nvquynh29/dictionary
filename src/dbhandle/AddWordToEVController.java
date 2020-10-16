package dbhandle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.Controller;
import sample.DatabaseConnection;
import sample.Word;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AddWordToEVController {
    @FXML
    Label lblEnglish;

    @FXML
    Label lblVietnamese;

    @FXML
    Label lblDescription;

    @FXML
    Label lblPronounce;

    @FXML
    TextField txtEnglish;

    @FXML
    TextField txtVietnamese;

    @FXML
    TextArea txtDescription;

    @FXML
    TextField txtPronounce;

    @FXML
    Button btnAdd;

    public Word newWord;

    public void getNewWord(ActionEvent event) {
        if (txtEnglish.getText().trim().isEmpty() || txtVietnamese.getText().trim().isEmpty()) {
            AlertController.showInfoAlert("Thông báo", null,
                    "Bạn phải nhập cả từ tiếng anh và tiếng việt!");
        } else {
            String word, html, vietnamese;
            String description = "", pronounce = "";
            description = txtDescription.getText();
            pronounce = txtPronounce.getText();
            word = txtEnglish.getText();
            vietnamese = txtVietnamese.getText();
            html = wordToHtml(word, vietnamese, description, pronounce);
            newWord = new Word(0, word, html, description, pronounce);
            if (newWord != null) {
                boolean isExisted = false;
                try {
                    ResultSet rs = DatabaseConnection.getResultSet("av");
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
                    AlertController.showInfoAlert("Thông báo", null, "Thêm từ mới thành công!");
                    sample.DatabaseConnection.addWordToDB("av", newWord);
                    Controller.setDictionaryEV();
                } else {
                    AlertController.showConfirmAlert("Xác nhận", word,
                            "Từ này đã có trong từ điển!\n" + "Bạn có muốn cập nhật?");
                    Alert alert = AlertController.getAlertConfirm();
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    ButtonType buttonAccept = new ButtonType("Xác nhận");
                    alert.getButtonTypes().setAll(buttonAccept, buttonTypeCancel);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonAccept) {
                        DatabaseConnection.updateWordToDB("av", word, html, description, pronounce);
                        Controller.setDictionaryEV();
                    }
                }
            }
        }
    }

    public static String wordToHtml(String word, String meaning, String description, String pronounce) {
        String result = "<h1>" + word
                + "</h1><h3><i>/" + pronounce
                + "/</i></h3><h2> + " + meaning
                + "</h2><ul><li>" + description
                + " </li></ul>";
        return result;
    }
}
