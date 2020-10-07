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
            showAlert("Bạn phải nhập cả từ tiếng anh và tiếng việt!");
        } else {
            String word, html;
            String description = "", pronounce = "";
            description = txtDescription.getText();
            pronounce = txtPronounce.getText();
            word = txtEnglish.getText();
            html = word + "\n\n" + description;
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
                    showAlert("Thêm từ mới thành công!");
                    sample.DatabaseConnection.addWordToDB("av", newWord);
                    Controller.setDictionaryEV();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận");
                    alert.setHeaderText(word);
                    alert.setContentText("Từ này đã có trong từ điển!\n Bạn có muốn cập nhật?");
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    ButtonType buttonAccept = new ButtonType("Xác nhận");
                    alert.getButtonTypes().setAll(buttonAccept, buttonTypeCancel);
                    Optional <ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonAccept) {
                        DatabaseConnection.updateWordToDB("av", word, html, description, pronounce);
                        Controller.setDictionaryEV();
                    }
                }
            }
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
