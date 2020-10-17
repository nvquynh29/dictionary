package dbhandle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import sample.Controller;
import sample.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteWordEVController {

    @FXML
    private TextField txtDelete;

    @FXML
    private Button btnDelete;

    public void deleteWord(ActionEvent event) {
        if (txtDelete.getText().trim().isEmpty()) {
            AlertController.showInfoAlert("Thông báo!", null,
                    "Bạn chưa nhập từ!");
        } else {
            String input = txtDelete.getText();
            boolean check = true;
            try {
                ResultSet rs = DatabaseConnection.getResultSet("av");
                while (rs.next()) {
                    if (rs.getString("word").equals(input)) {
                        DatabaseConnection.deleteWordDB("av", input);
                        Controller.setDictionaryEV();
                        check = false;
                        break;
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (check) AlertController.showInfoAlert("Thông báo!", null,
                    "Từ bạn nhập không tồn tại!");
        }
    }
}
