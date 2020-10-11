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

public class DeleteWordVEController {

    @FXML
    private TextField txtDelete;

    @FXML
    private Button btnDelete;

    public void deleteWord(ActionEvent event) {
        if (txtDelete.getText().trim().isEmpty()) {
            showAlert("You haven't entered the word!");
        } else {
            String input = txtDelete.getText();
            boolean check = true;
            try {
                ResultSet rs = DatabaseConnection.getResultSet("va");
                while (rs.next()) {
                    if (rs.getString("word").equals(input)) {
                        DatabaseConnection.deleteWordDB("va", input);
                        Controller.setDictionaryVE();
                        check = false;
                        break;
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (check) showAlert("Word you entered isn't exist!");
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
