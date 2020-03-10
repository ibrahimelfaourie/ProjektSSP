package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


public class FriendListController {

    String[] friends;
    int userId;

    DbHandler dbh;

    public FriendListController(){
        dbh = new DbHandler();
        dbh.initConection();
    }


    @FXML
    ListView friendList ;
    @FXML
    Button utmanaButton;
    @FXML
    ListView requests;
    @FXML
    ListView activeGamesView;
    @FXML
    Button tackaNej, tackaJa, logout_button;


    public void setFriends(String[] newFriends){
        friends = newFriends;
        ObservableList<String> list = FXCollections.observableArrayList(friends);
        friendList.setItems(list);
        friendList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void challengeFriend(ActionEvent event) throws SQLException {

        try {


        String opponent = (String) friendList.getSelectionModel().getSelectedItem();
        int opponentId = dbh.findUserId(opponent);



            FXMLLoader loader = new FXMLLoader(getClass().getResource("roundsPage.fxml"));

            Parent roundsParent = loader.load();
            Scene roundsScene = new Scene(roundsParent);

            RoundsController flc = loader.getController();
            flc.setPlayers(userId, opponentId);


            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(roundsScene);
            window.show();

     } catch (Exception e){
        e.printStackTrace();
        }
    }
    public void setUserId(int uId) throws SQLException {
        userId = uId;
        String[] requestedPlayer = dbh.findRequestsForPlayer(uId);
        if (requestedPlayer.length>0){
            setRequests(requestedPlayer);
        }
        String[] friends = dbh.ShowFriends(userId);
        setFriends(friends);

    }
    public void setRequests(String[] requestsFromPlayer){

        ObservableList<String> list = FXCollections.observableArrayList(requestsFromPlayer);
        requests.setItems(list);
        requests.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void acceptChallenge(ActionEvent event){

        String opponent = (String) friendList.getSelectionModel().getSelectedItem();
        if (opponent!= null){





        }
    }
    public void setActiveGamesView(String[] requestsFromPlayer){

        ObservableList<String> list = FXCollections.observableArrayList(requestsFromPlayer);
        activeGamesView.setItems(list);
        activeGamesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void logout(ActionEvent event) {
        Stage stage;
        Parent root;

        try {
            stage = (Stage) logout_button.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("loginPage.fxml"));

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
