package dbhandle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.Controller;
import sample.DatabaseConnection;
import sample.Word;

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

    public void addNewWord(ActionEvent event) {
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
            html = AddWordToEVController.wordToHtml(word, vietnamese, description, pronounce);
            if (DatabaseConnection.isContains("av", word)) {
                AlertController.showConfirmAlert("Xác nhận", "Từ này đã có trong từ điển!\n"
                        + "Bạn có muốn cập nhật?", null);
                Alert alert = AlertController.getAlertConfirm();
                ButtonType buttonCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType buttonAccept = new ButtonType("Xác nhận");
                alert.getButtonTypes().setAll(buttonAccept, buttonCancel);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get().equals(buttonAccept)) {
                    DatabaseConnection.updateWordToDB("av", word, html, description, pronounce);
                }
            } else {
                DatabaseConnection.addWordToDB("av", word, html, description, pronounce);
                AlertController.showInfoAlert("Thông báo", null, "Thêm từ mới thành công!");
            }
            Controller.trieEV.insert(word, html);
            Stage current = (Stage) txtEnglish.getScene().getWindow();
            current.close();
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
