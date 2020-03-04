package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Controller {

    DbHandler dbH = new DbHandler();

    @FXML
    javafx.scene.control.TextField username_Input;
    @FXML
    PasswordField password_Input;
    @FXML
    javafx.scene.control.Button login_Button;

    public Controller() {
        dbH.initConection();
    }

    public void checkLogin(ActionEvent event) throws IOException, SQLException {
        String userNameInput = username_Input.getText();
        String passwordInput = password_Input.getText();
        int result = dbH.login(userNameInput, passwordInput);
        if (result > 0) {

            login_Button.setText("Ok");
            Parent friendListParent = FXMLLoader.load(getClass().getResource("friendListPage.fxml"));
            Scene friendListScene = new Scene(friendListParent);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(friendListScene);
            window.show();


        } else {
            login_Button.setText("Fail");
        }

    }

}
