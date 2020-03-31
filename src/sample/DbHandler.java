package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

// dENNA KLASS FÖR  all komunikation melland app och databas.
// Init conection upprätter förbindelsen till våran databas
// connection har vi global så vi slipper koppla upp oss i varje metod flera grr
// Login metoden är en int för


public class DbHandler {
    //matris
    int[][] outcome;

    public DbHandler() {
        initConection();
        outcome = new int[4][4];
        outcome[1][1] = 0;
        outcome[2][2] = 0;
        outcome[3][3] = 0;

        outcome[1][2] = 1;
        outcome[1][3] = 2;

        outcome[2][1] = 2;
        outcome[2][3] = 1;

        outcome[3][1] = 1;
        outcome[3][2] = 2;

    }

    private Connection connection = null;


    public void initConection() {

        try {

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sspDB", "Admin", "123456");

            connection.setAutoCommit(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // får vi en träff blir resultatet större än 0
    public int login(String username, String password) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("SELECT COUNT(*) AS total FROM \"Users\"" +
                    " where \"Name\" = ? and \"Password\" = ?; ");


            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            int antal = 0;
            // tar fram det första objektet i resultat
            if (rs.next()) {
                antal = rs.getInt("total");
                // detta gör att de blir mer än 0
            }
            return antal;

        } catch (Exception e) {
            System.out.println(e.getMessage());


        }
        // om vi får ett värde stänger vi queryn
        if (stmt != null) {
            stmt.close();
        }
        return 0;
        //  om vi får fel så returnar den 0
    }

    // Efter att vi loggat in behöver vi userID till perosnen för de övriga kommandon(Utgår från namn)
    public int findUserId(String name) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("SELECT \"UserID\" from \"Users\" where \"Name\" = ?; ");
        stmt.setString(1, name);
        int userId = getSingelInt(stmt, "UserID");
        return userId;
    }

    // Tvärt om förregående metod..vi vill ha namnet på ett userid på t.ex våra listor (då vi utgår från id till dom olika funktiornea)
    public String findUserName(int userId) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("SELECT \"Name\" from \"Users\" where \"UserID\" = ?; ");
        stmt.setInt(1, userId);

        String userName = getSingelString(stmt, "Name");

        return userName;
    }

    // hjälpmetod: när vi vill få en Integer
    private int getSingelInt(PreparedStatement stmt, String column) {

        try {
            ResultSet validUser = stmt.executeQuery();

            if (validUser.next()) {
                int result = validUser.getInt(column);

                return result;
            } else return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Hjälpmetod: så att vi slipper skriva samma komandon flera grr.
    // Vi vill få en sträng
    private String getSingelString(PreparedStatement stmt, String column) {

        try {
            ResultSet validUser = stmt.executeQuery();

            if (validUser.next()) {
                String result = validUser.getString(column);

                return result;
            } else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hitta en specefiks persons vänner utifrån dennes userID.
    public String[] ShowFriends(int userId) throws SQLException {

        try {
            PreparedStatement stmt = connection.prepareStatement
                    ("SELECT \"Name\" FROM \"Users\" inner join \"FriendList\" on \"Users\".\"UserID\" = \"FriendList\".\"FriendID\"\n" +
                            "where \"Owner\" = ? ;");

            stmt.setInt(1, userId);
            ResultSet validUser = stmt.executeQuery();
            ArrayList<String> namelist = new ArrayList<String>();
            // Vi får ett ett antal namn, vi vet inte i förväg hur många
            // Vi stoppar in i arraylist först sen skapar vi en array utifrån storleken.

            while ((validUser.next())) {
                String name = validUser.getString("Name");
                namelist.add(name);
                // så länge de finns fler resultat att hämta så läggs den i namelist

            }
            String[] array = new String[namelist.size()];
            array = namelist.toArray(array);
            // vi flyttar över från arraylist till array
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // vi lägger till en request från en spelare1(utmanaren) till spelare2(mottagaren)
    // den som anropar sätter in värdena.
    public void addRequests(int player1, int player2, int rounds) {
        try {


            PreparedStatement stmt = connection.prepareStatement("insert into \"Request\"(\"Player1\" , \"Player2\" , \"Acceptance\", \"rounds\" ) values(?,?,?,?)");
            stmt.setInt(1, player1);
            stmt.setInt(2, player2);
            stmt.setInt(3, 0);
            stmt.setInt(4, rounds);


            int result = stmt.executeUpdate();
            connection.commit();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // hittar antalet requests mellan två spelare
    public int findActiveRequests(int player1, int player2) {
        try {

            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) AS total FROM \"Request\" WHERE \"Player1\" = ? AND \"Player2\" = ? AND \"Acceptance\" = ?;  ");
            stmt.setInt(1, player1);
            stmt.setInt(2, player2);
            stmt.setInt(3, 0);

            int number1 = getSingelInt(stmt, "total");

            PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) AS total FROM \"Request\" WHERE \"Player2\" = ? AND \"Player1\" = ? AND \"Acceptance\" = ?;  ");
            stmt2.setInt(1, player1);
            stmt2.setInt(2, player2);
            stmt2.setInt(3, 0);

            int number2 = getSingelInt(stmt2, "total");

            return number1 + number2;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Den hittar alla aktiva requests som en spelare har fått där man inte ännu accepterat eller nekat. (Acceptance = 0)
    public String[] findRequestsForPlayer(int playerId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("select \"Name\" from \"Users\" inner join \"Request\" on " +
                    "\"Request\".\"Player1\" = \"Users\".\"UserID\" where \"Player2\" = ? and \"Acceptance\" = ?");
            stmt.setInt(1, playerId);
            stmt.setInt(2, 0);
            String[] requestPlayer = getStringArray(stmt, "Name");
            return requestPlayer;
        } catch (Exception e) {
            return null;
        }
    }

    // hjälpmetod: när man röknar med att få mer än ett resultat (strängar)
    // dvs hämtar flera reslautat
    // använder först arraylist som sen konverteras till array
    private String[] getStringArray(PreparedStatement stmt, String column) {

        try {
            ResultSet validUser = stmt.executeQuery();
            ArrayList<String> namelist = new ArrayList<String>();

            while ((validUser.next())) {
                String name = validUser.getString(column);
                namelist.add(name);

            }
            String[] array = new String[namelist.size()];
            array = namelist.toArray(array);
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // återigen hjälpmetod: När vi hämtar flera integers
    private int[] getIntArray(PreparedStatement stmt, String column) {

        try {
            ResultSet validUser = stmt.executeQuery();
            ArrayList<Integer> namelist = new ArrayList<Integer>();

            while ((validUser.next())) {
                int name = validUser.getInt(column);
                namelist.add(name);

            }
            int[] array = new int[namelist.size()];
            for (int i = 0; i < namelist.size(); i++) {
                array[i] = namelist.get(i);
            }

            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // hjälpmetod: när vi bara vill updatera ett värde utan returna något
    private void sendUpdate(PreparedStatement stmt) {
        try {
            int result = stmt.executeUpdate();
        } catch (Exception e) {

        }
    }

    // registrerar ett nytt spel i databasen.
    // utgår från vilken request
    // gamestaus = 0 innebär att spelet körs och inte klart
    // winnerid null då ingen vunnit än
    public void addNewGame(int requestId) {

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"Game\"(\"RequestID\", \"Gamestatus\", \"winnerid\") values(?,?,NULL); ");
            stmt.setInt(1, requestId);
            stmt.setInt(2, 0);
            // stmt.setInt(3, 0);

            stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Vi vet att player1 utmanat player2 och vi vill hitta det requestID för att föra vidare till match tabellen
    public int findRequestId(int player1, int player2) {

        try {

            PreparedStatement stmt = connection.prepareStatement("SELECT \"RequestID\" FROM \"Request\" WHERE \"Player1\"= ? AND \"Player2\"= ? ");
            stmt.setInt(1, player1);
            stmt.setInt(2, player2);
            int rid = getSingelInt(stmt, "RequestID");
            return rid;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }


    }

    // Uppdaterar aceptance i requesttabellen dvs när en spelare accepterar en request
    public void acceptRequest(int requestId) {

        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE \"Request\" SET \"Acceptance\" = ? WHERE \"RequestID\"= ? ;");
            stmt.setInt(1, 1);
            stmt.setInt(2, requestId);

            stmt.executeUpdate();
            connection.commit();
            // vi använder commit när vi ska ändra något
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // uppdaterar aceptance i requesttabellen dvs när man nekar en request
    public void declineRequest(int requestId) {

        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE \"Request\" SET \"Acceptance\" = ? WHERE \"RequestID\"= ? ;");
            stmt.setInt(1, 2);
            stmt.setInt(2, requestId);

            stmt.executeUpdate();
            connection.commit();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // den hittar aktiva alla matcher som en spelare är inblandad i.
    // vi får en lista med namn på alla spelare man har match mot
    // vi vill hitta bägge kombinationerna
    public String[] findGamesForPlayer(int userId) {

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT \"Player1\" FROM \"Request\" INNER JOIN \"Game\" ON " +
                    "\"Game\".\"RequestID\"= \"Request\".\"RequestID\" WHERE \"Player2\"= ? AND \"Gamestatus\"= 0;");
            stmt.setInt(1, userId);

            String[] array1 = getStringArray(stmt, "Player1");
            PreparedStatement stmt2 = connection.prepareStatement("SELECT \"Player2\" FROM \"Request\" INNER JOIN \"Game\" ON " +
                    "\"Game\".\"RequestID\"= \"Request\".\"RequestID\" WHERE \"Player1\"= ? AND \"Gamestatus\"= 0;");
            stmt2.setInt(1, userId);
            String[] array2 = getStringArray(stmt2, "Player2");

            ArrayList<String> aL1 = new ArrayList<>(Arrays.asList(array1));
            // där jag har utmanat andra

            ArrayList<String> aL2 = new ArrayList<>(Arrays.asList(array2));
            // där andra utmanat mig

            aL1.removeAll(aL2);
            // ett trick för att få med från den ena till den andra utan att få kopior
            aL1.addAll(aL2);

            LinkedHashSet<String> hs = new LinkedHashSet<>(aL1);
            // allt som finns med 2grr tas bort automatiskt
            aL1 = new ArrayList<>(hs);

            // Det slutgiltiga det vi slog ihop i de förregånde hamna i array3
            String[] array3 = new String[aL1.size()];
            array3 = aL1.toArray(array3);

            String[] names = new String[array3.length];
            // i arrayerna ligger bara userid så vi vill få namnen på useriden
            // vi får in namnen med hjälp av metoden finduserName

            for (int i = 0; i < array3.length; i++) {

                int pId = Integer.parseInt(array3[i]);
                String name = findUserName(pId);
                names[i] = name;
            }
            return names;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public int findGameId(int requestId) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("SELECT \"GameID\" FROM \"Game\" WHERE \"RequestID\"= ? ");
        stmt.setInt(1, requestId);

        int gid = getSingelInt(stmt, "GameID");
        return gid;

    }

    public int findNumberRoundsForGame(int gameId) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT \"rounds\" FROM \"Request\" INNER JOIN \"Game\" ON" +
                "\"Game\".\"RequestID\"= \"Request\".\"RequestID\" WHERE \"GameID\"= ?");
        stmt.setInt(1, gameId);
        int rounds = getSingelInt(stmt, "rounds");
        return rounds;

    }

    public String[] findNamesForGamePage(int gameId) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("SELECT \"Player1\" FROM \"Request\" INNER JOIN \"Game\" ON" +
                "\"Game\".\"RequestID\"= \"Request\".\"RequestID\" WHERE \"GameID\"= ?");
        stmt.setInt(1, gameId);
        int player1 = getSingelInt(stmt, "Player1");
        String name1 = findUserName(player1);

        PreparedStatement stmt2 = connection.prepareStatement("SELECT \"Player2\" FROM \"Request\" INNER JOIN \"Game\" ON" +
                "\"Game\".\"RequestID\"= \"Request\".\"RequestID\" WHERE \"GameID\"= ?");
        stmt2.setInt(1, gameId);
        int player2 = getSingelInt(stmt2, "Player2");
        String name2 = findUserName(player2);

        String[] arrayNames = new String[2];
        arrayNames[0] = name1;
        arrayNames[1] = name2;

        return arrayNames;

    }

    public int playerChoice(int gameId, int round, int playerNumber, int choice) throws SQLException {

        boolean cm = choiceMade(gameId, round, 3 - playerNumber);

        String playerColumn;
        if (playerNumber == 1) {
            playerColumn = "p1choice";
        } else {
            playerColumn = "p2choice";
        }

        if (cm) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE \"MatchLog\" SET " + playerColumn + " = ? WHERE \"GameID\" = ?" +
                    "AND \"round\" = ?");
            stmt.setInt(1, choice);
            stmt.setInt(2, gameId);
            stmt.setInt(3, round);

            stmt.executeUpdate();
            connection.commit();
            stmt.close();
            int otherPlayersChoice = findChoice(gameId, round, 3 - playerNumber);
            int result;
            if (playerNumber == 1) {
                result = calculateResult(choice, otherPlayersChoice);
            } else {
                result = calculateResult(otherPlayersChoice, choice);
            }
            if (result > 0) {

                updateScore(gameId, round, result);
            } else {
                removeRound(gameId, round);
            }
            return result;
        } else {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"MatchLog\"(\"GameID\"," + playerColumn + ",\"round\") VALUES(?, ?, ?)");
            stmt.setInt(1, gameId);
            stmt.setInt(2, choice);
            stmt.setInt(3, round);

            stmt.executeUpdate();
            connection.commit();
            stmt.close();
            return -1;

        }


    }

    private void removeRound(int gameId, int round) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM \"MatchLog\" WHERE \"GameID\"= ? AND \"round\" = ?");
        stmt.setInt(1, gameId);
        stmt.setInt(2, round);
        stmt.executeUpdate();
        connection.commit();
        stmt.close();

    }

    private int findChoice(int gameId, int round, int playerNumber) throws SQLException {

        String playerColumn;
        if (playerNumber == 1) {
            playerColumn = "p1choice";
        } else {
            playerColumn = "p2choice";
        }

        PreparedStatement stmt = connection.prepareStatement("SELECT " + playerColumn + " FROM \"MatchLog\" WHERE \"GameID\" = ? AND \"round\" = ? ");
        stmt.setInt(1, gameId);
        stmt.setInt(2, round);
        int choice = getSingelInt(stmt, playerColumn);
        return choice;

    }

    public boolean choiceMade(int gameId, int round, int playerNumber) throws SQLException {

        String playerColumn;
        if (playerNumber == 1) {
            playerColumn = "p1choice";
        } else {
            playerColumn = "p2choice";
        }
        PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) AS total FROM \"MatchLog\" WHERE \"GameID\"= ? AND \"round\"= ? AND " + playerColumn + ">0");
        stmt.setInt(1, gameId);
        stmt.setInt(2, round);
        int result = getSingelInt(stmt, "total");
        if (result > 0) {
            return true;
        } else {
            return false;
        }

    }

    private int calculateResult(int p1Choice, int p2Choice) {

        return outcome[p1Choice][p2Choice];
    }

    public int findNumberOfRoundsPlayed(int gameId) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT MAX(round) AS maxrounds FROM \"MatchLog\" WHERE \"GameID\" = ? AND \"Score\">0");
        stmt.setInt(1, gameId);
        int result = getSingelInt(stmt, "maxrounds");
        return result;
    }


    public void updateScore(int gameId, int round, int score) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("UPDATE \"MatchLog\" SET \"Score\" = ? WHERE \"GameID\" = ? AND \"round\" = ? ");
        stmt.setInt(1, score);
        stmt.setInt(2, gameId);
        stmt.setInt(3, round);

        stmt.executeUpdate();
        connection.commit();
        stmt.close();
    }

    public int[] getscoresForGame(int gameId) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT \"Score\" FROM \"MatchLog\" WHERE \"GameID\"= ? AND \"Score\">0");
        stmt.setInt(1, gameId);
        int[] results = getIntArray(stmt, "Score");
        return results;
    }

    public void setWinnerOfGame(int gameId, int winnerPlayer) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("UPDATE \"Game\" SET \"winnerid\"= ? WHERE \"GameID\"= ?");

        stmt.setInt(1, winnerPlayer);
        stmt.setInt(2, gameId);
        stmt.executeUpdate();
        connection.commit();
        stmt.close();

        PreparedStatement stmt2 = connection.prepareStatement("UPDATE \"Game\" SET \"Gamestatus\"= 1 WHERE \"GameID\"= ?");

        stmt2.setInt(1, gameId);
        stmt2.executeUpdate();
        connection.commit();
        stmt2.close();

    }


}
