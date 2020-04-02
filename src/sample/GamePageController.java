package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.SQLException;


public class GamePageController {

    int gameId;
    int currentRound;
    int playerId;
    int playerNumber;
    String winner = "win";
    String looser = "lost";
    DbHandler dbh;
    int numberRounds;
    int p1Wins;
    int p2Wins;
    int opponentId;

    @FXML
    Button backButton, rockButton, scissorsButton, paperButton;
    @FXML
    Label playerOne, playerTwo, roundsLabel, player1Choice, player2Choice;
    @FXML
    ListView resultListp1, resultListp2;

    public GamePageController() {
        dbh = new DbHandler();
        p1Wins = 0;
        p2Wins = 0;
    }

    public void setGameId(int gamesId) throws SQLException {
        gameId = gamesId;

        setUpRounds();

    }

    public void setPlayerId(int player, int opponent) throws SQLException {
        playerId = player;
        opponentId = opponent;

        setUpNames();


    }

    private void setUpRounds() throws SQLException {

        numberRounds = dbh.findNumberRoundsForGame(gameId);
        updateRoundNumber();
        if (numberRounds > 0) {
            int[] results = dbh.getscoresForGame(gameId);
            for (int i = 0; i < results.length; i++) {
                updateResultLists(results[i]);
            }
        }


    }

    private void setUpNames() throws SQLException {
        String[] names = dbh.findNamesForGamePage(gameId);

        playerOne.setText(names[0]);
        playerTwo.setText(names[1]);

        int p1Id = dbh.findUserId(names[0]);
        if (playerId == p1Id) {
            playerNumber = 1;
        } else {
            playerNumber = 2;
        }


    }

    public void rockButton(ActionEvent event) throws SQLException {

        int choice = 1;
        makeChoice(choice);

    }

    public void siscorButton(ActionEvent event) throws SQLException {

        int choice = 2;
        makeChoice(choice);

    }

    public void paperButton(ActionEvent event) throws SQLException {

        int choice = 3;
        makeChoice(choice);

    }

    private void makeChoice(int choice) throws SQLException {

        if (dbh.choiceMade(gameId, currentRound, playerNumber)) {
            display("You already made your choice");
        } else {
            int result = dbh.playerChoice(gameId, currentRound, playerNumber, choice);
            if (result == -1) {
                display("Choice made! waiting for oppenents choice");
            } else {
                if (result == playerNumber) {
                    display("You won the round!");
                } else if (result == 0) {
                    display("The round is a draw, Play again!");
                } else {
                    display("You lost the round!");
                }
            }
            if (result > 0) {
                updateResultLists(result);
                if (p1Wins >= (numberRounds + 1) / 2) {
                    if (playerNumber == 1) {
                        display("You won the game");
                        dbh.setWinnerOfGame(gameId, playerId);
                    } else {
                        display("You lost the game");
                        dbh.setWinnerOfGame(gameId, opponentId);
                    }
                    backToFriendListSceene();
                } else if (p2Wins >= (numberRounds + 1) / 2) {

                    if (playerNumber == 2) {
                        display("You won the game");
                        dbh.setWinnerOfGame(gameId, playerId);
                    } else {
                        display("You lost the game");
                        dbh.setWinnerOfGame(gameId, opponentId);
                    }
                    backToFriendListSceene();
                } else {
                    updateRoundNumber();
                }

            }
        }
    }

    private void updateResultLists(int result) {

        //https://stackoverflow.com/questions/32700005/javafx-listview-add-and-edit-element

        if (result == 1) {

            p1Wins++;
            resultListp1.getItems().add(resultListp1.getItems().size(), winner);
            resultListp1.scrollTo(winner);
            resultListp1.edit(resultListp1.getItems().size() - 1);

            resultListp2.getItems().add(resultListp2.getItems().size(), looser);
            resultListp2.scrollTo(looser);
            resultListp2.edit(resultListp2.getItems().size() - 1);
        } else {
            p2Wins++;
            resultListp1.getItems().add(resultListp1.getItems().size(), looser);
            resultListp1.scrollTo(looser);
            resultListp1.edit(resultListp1.getItems().size() - 1);

            resultListp2.getItems().add(resultListp2.getItems().size(), winner);
            resultListp2.scrollTo(winner);
            resultListp2.edit(resultListp2.getItems().size() - 1);

        }
    }

    private void updateRoundNumber() throws SQLException {
        int playedRounds = dbh.findNumberOfRoundsPlayed(gameId);
        int roundsLeft = numberRounds - playedRounds;
        roundsLabel.setText(roundsLeft + " Rounds left");
        currentRound = playedRounds + 1;
    }

    public void display(String message)
    //http://www.learningaboutelectronics.com/Articles/How-to-create-a-pop-up-window-in-JavaFX.php
    //popup fönster
    {
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


    public void backButton(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("friendListPage.fxml"));

            Parent friendListParent = loader.load();
            Scene friendListScene = new Scene(friendListParent);

            FriendListController flc = loader.getController();
            flc.setUserId(playerId);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(friendListScene);
            window.show();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    public void backToFriendListSceene() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("friendListPage.fxml"));

            Parent friendListParent = loader.load();
            Scene friendListScene = new Scene(friendListParent);

            FriendListController flc = loader.getController();
            flc.setUserId(playerId);

            Stage window = (Stage) ((Node) backButton).getScene().getWindow();
            window.setScene(friendListScene);
            window.show();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


}
