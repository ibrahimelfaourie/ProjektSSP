package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
//import org.intellij.lang.annotations.Language.*;

import java.io.IOException;
import java.sql.SQLException;


public class FriendListController {

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
    @FXML
    Button enterGameButton;
    @FXML
    Label totalWins, totalLoss, totalGames;


    // En person som loggat in så vi hämtar den personens vänner och lägger in namnen i friendlist listan
    // för att kunna lägga in i listviewn använder vi oss av observablelist
    public void setFriends(String[] friends){
        //http://www.java2s.com/Tutorials/Java/JavaFX/0640__JavaFX_ListView.htm
        ObservableList<String> list = FXCollections.observableArrayList(friends);
        friendList.setItems(list);
        friendList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    //vi väljer en vän och trycka på utmana sen (kopplad till knappen challnge)
    // anropar findUserid och har oppenent som parameter(oppenent är den vi klickat på i listan)
    // sen kollar vi om vi redan utmanat någon vi ska få ett felmadelande om vi gjort det.
    // om vi inte gjort det dvs request == 0 så laddar ni in den nya scenen för att välja rounds
    // vi skickar med userid och oppentid till den nya controllern för den nya scenen
    public void challengeFriend(ActionEvent event) throws SQLException {

        try {

            String opponent = (String) friendList.getSelectionModel().getSelectedItem();
            int opponentId = dbh.findUserId(opponent);

            int requests = dbh.findActiveRequests(userId, opponentId);

            if (requests== 0){

                int activeGames = dbh.findActiveGames(userId,opponentId);
                if (activeGames==0) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("roundsPage.fxml"));
                    Parent roundsParent = loader.load();
                    Scene roundsScene = new Scene(roundsParent);

                    RoundsController flc = loader.getController();
                    flc.setPlayers(userId, opponentId);

                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(roundsScene);
                    window.show();
                }else {
                    popupMessage("You already in game with this friend");
                }

            }else{

                popupMessage("You already challenged this friend");


            }
        } catch (Exception e){
        e.printStackTrace();
        }
    }
    //en spelares information
    // Den här hämtar alla requests,vänner och matcher.
    public void setUserId(int uId) throws SQLException {
        userId = uId;
        String[] requestedPlayer = dbh.findRequestsForPlayer(uId);
        if (requestedPlayer.length>0){
            setRequests(requestedPlayer);
        }
        String[] friends = dbh.ShowFriends(userId);
        setFriends(friends);
        updateGameList();
        updateStatistics();

    }

    // hämtar requests och lägger in dom i requestlistan.
    // den hämtar det som finns i databasen
    // vi anropar det när vi ändrat i listan dvs accepterat eller nekat
    private void updateRequestList(){
        String[] requestedPlayer = dbh.findRequestsForPlayer(userId);
        setRequests(requestedPlayer);
    }

    // hämtar games i lägger in i gameslistan
    // vi andropar denna metod när vi acceptaret en request
    // även när vi spelat klart ett spel
    private void updateGameList(){

        String[] gamesForPlayer = dbh.findGamesForPlayer(userId);
        setActiveGamesView(gamesForPlayer);


    }

    // namn läggs in i requestlsitan
    public void setRequests(String[] requestsFromPlayer){
        //http://www.java2s.com/Tutorials/Java/JavaFX/0640__JavaFX_ListView.htm
        ObservableList<String> list = FXCollections.observableArrayList(requestsFromPlayer);
        requests.setItems(list);
        requests.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    // (acceptera knappen)när man accepterat en challenge
    // oppenent är det namn man valt
    // sen hittar personens id (finsuserID metoden)
    // sen hittar vi requestid (findrequestID metoden parametrarna är utmaneren och mottagere)
    // sen anropar vi addnewgame med reqid som paremter
    // sen andopar acceptrequest (ändrar acceptance till 1)
    // sen uppdaterar vi listorna i interfacet
    public void acceptChallenge(ActionEvent event) throws SQLException {

        String opponent = (String) requests.getSelectionModel().getSelectedItem();

        if (opponent!= null){

        int oppenentId = dbh.findUserId(opponent);
        int requestId = dbh.findReqIdUnAccepted(oppenentId, userId);

            dbh.addNewGame(requestId);

            dbh.acceptRequest(requestId);


            updateGameList();
            updateRequestList();



        }
    }

    // som förregånde metod
    public void declineChallenge(ActionEvent event) throws SQLException {

        String opponent = (String) requests.getSelectionModel().getSelectedItem();

        if (opponent!= null){

            int oppenentId = dbh.findUserId(opponent);
            int requestId = dbh.findRequestId(oppenentId, userId);

            dbh.declineRequest(requestId);


            updateRequestList();



        }
    }
    // vi stoppar namnen i gamelistan
    public void setActiveGamesView(String[] gamesFromPlayer){

        ObservableList<String> list = FXCollections.observableArrayList(gamesFromPlayer);
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
    public void enterGame(ActionEvent event) throws SQLException, IOException {

        String opponent = (String) activeGamesView.getSelectionModel().getSelectedItem();

        if(opponent != null){

            int oppenentId = dbh.findUserId(opponent);
            int requestId = dbh.findRequestId(oppenentId, userId);

            if(requestId == -1){
                requestId = dbh.findRequestId(userId,oppenentId);
            }

           int gameId = dbh.findGameId(requestId);


            if (gameId!= 0){

                FXMLLoader loader = new FXMLLoader(getClass().getResource("GamePage.fxml"));

                Parent roundsParent = loader.load();
                Scene roundsScene = new Scene(roundsParent);


                GamePageController flc = loader.getController();
                flc.setGameId(gameId);
                flc.setPlayerId(userId,oppenentId);


                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(roundsScene);
                window.show();

            }else{

                // ett felmedelande
            }


        }

    }

    public void updateStatistics() throws SQLException {

        int nrGames = dbh.findNumberOfGames(userId);
        totalGames.setText(String.valueOf(nrGames));

        int nrWins = dbh.findNumberOfWins(userId);
        totalWins.setText(String.valueOf(nrWins));

        int nrLoss = dbh.findNumberOfLoss(userId);
        totalLoss.setText(String.valueOf(nrLoss));


    }

    public void popupMessage(String message){

        Stage popupwindow = new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("This is a pop up window");
        Label label1 = new Label(message);

        Button button1 = new Button("Close");


        button1.setOnAction(e -> popupwindow.close());


        VBox layout = new VBox(10);


        layout.getChildren().addAll(label1, button1);

        layout.setAlignment(Pos.CENTER);

        Scene scene1 = new Scene(layout, 300, 250);

        popupwindow.setScene(scene1);

        popupwindow.showAndWait();


    }


}
