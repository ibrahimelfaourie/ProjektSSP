package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


public class RoundsController {


    int [] rounds;
    int player1;
    int player2;

    @FXML
    ListView roundsView ;
    @FXML
    Button sendRequest, flee;


    DbHandler dbh;


    public RoundsController(){

        dbh = new DbHandler();
        rounds = new int[]{3, 5, 7, 9};
        dbh.initConection();

    }

    // när man valt hur många rundor och då sickar vi requesten
    // och llägger in  informationen i datatbasen sen tillbaka till friendlistan
    // vi andropar setuserid och skapar en ny friendlist sida om vilekn spelare det gäller
    public void buttonSendRequest(ActionEvent event) throws IOException, SQLException {

        int selectedIndex = roundsView.getSelectionModel().getSelectedIndex();

        int numberRounds = rounds[selectedIndex];

        dbh.addRequests(player1, player2, numberRounds);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("friendListPage.fxml"));

        Parent friendListParent = loader.load();
        Scene friendListScene = new Scene(friendListParent);

        FriendListController flc = loader.getController();
        flc.setUserId(player1);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(friendListScene);
        window.show();

    }

    // denna metod skickar information till  rounds kontorller, skickar in vilka spelare det handlar om
    public void setPlayers(int p1, int p2){
        player1 = p1;
        player2 = p2;

        // vi skapar vilka alternativ det finns och lägger in dom i listviewn (roundsview)
        String[] roundsStrings = new String[]{"3", "5", "7", "9"};

        ObservableList<String> list = FXCollections.observableArrayList(roundsStrings);
        roundsView.setItems(list);
        roundsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void fleeButton(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("friendListPage.fxml"));

            Parent friendListParent = loader.load();
            Scene friendListScene = new Scene(friendListParent);

            FriendListController flc = loader.getController();
            flc.setUserId(player1);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(friendListScene);
            window.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

}
