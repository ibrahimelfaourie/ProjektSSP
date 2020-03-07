package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller {

    DbHandler dbH = new DbHandler();

    @FXML
    javafx.scene.control.TextField username_Input;
    @FXML
    PasswordField password_Input;
    @FXML
    javafx.scene.control.Button login_Button;
    @FXML
    Label failedloggin;
    @FXML
    Label friendLabel1;
    @FXML
    Label friendLabel2;
    @FXML
    Label friendLabel3;
    @FXML
    Label friendLabel4;


    public Controller() {
        dbH.initConection();
    }

    public void checkLogin(ActionEvent event) throws IOException, SQLException {
        String userNameInput = username_Input.getText();
        String passwordInput = password_Input.getText();
        int result = dbH.login(userNameInput, passwordInput);
        if (result > 0) {

           // login_Button.setText("Ok");
           // String [] friends = dbH.ShowFriends(1);
            int userId = dbH.findUserId(userNameInput);
            //login_Button.setText(String.valueOf(userId));

            String[] friends = dbH.ShowFriends(userId);


            FXMLLoader loader = new FXMLLoader(getClass().getResource("friendListPage.fxml"));

            Parent friendListParent = loader.load();
            Scene friendListScene = new Scene(friendListParent);

            FriendListController flc = loader.getController();
            flc.setUserId(userId);
            flc.setFriends(friends);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(friendListScene);
            window.show();




        } else {
            failedloggin.setText("Fail to loggin, name or password incorrect!");
        }

    }
    public void friendList(ActionEvent event){

       // int result = dbH.ShowFriends();
    }

}
