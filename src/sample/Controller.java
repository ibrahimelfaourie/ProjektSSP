package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller {

   DbHandler dbH;

    @FXML
    javafx.scene.control.TextField username_Input;
    @FXML
    PasswordField password_Input;
    @FXML
    javafx.scene.control.Button login_Button;
    @FXML
    Label failedloggin;
    @FXML
    Button exit_button;

    // anropar initconection i controller klassen

    public Controller() {
        dbH = new DbHandler();
        dbH.initConection();
    }

    // vi kollar att usernamne att passwoed finns och om resultatet större än 0
    // vi anropar sedan finduserID FÖR ATTT HITT id på den som loggat in sen byter vi scen
    // vi anropar metoden setuserid som skickar
    public void checkLogin(ActionEvent event) throws IOException, SQLException {
        String userNameInput = username_Input.getText();
        String passwordInput = password_Input.getText();
        int result = dbH.login(userNameInput, passwordInput);
        if (result > 0) {


            int userId = dbH.findUserId(userNameInput);



            FXMLLoader loader = new FXMLLoader(getClass().getResource("friendListPage.fxml"));

            Parent friendListParent = loader.load();
            Scene friendListScene = new Scene(friendListParent);

            FriendListController flc = loader.getController();
            flc.setUserId(userId);


            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(friendListScene);
            window.show();




        } else {
            failedloggin.setText("Fail to loggin, name or password incorrect!");
        }

    }

    public void exitButton(ActionEvent event){

        Stage stage = (Stage) exit_button.getScene().getWindow();
        stage.close();

    }



}
