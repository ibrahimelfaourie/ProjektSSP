package sample;

import java.sql.*;
import java.util.ArrayList;

public class DbHandler {

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

    public int login(String username, String password) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("SELECT COUNT(*) AS total FROM \"Users\"" +
                    " where \"Name\" = ? and \"Password\" = ?; ");


            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            // ResultSet validUser = stmt.executeQuery();
            int antal = -1;
            if (rs.next()) {
                antal = rs.getInt("total");
            }
            return antal;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // return 0;

        }
        if (stmt != null) {
            stmt.close();
        }
        return 0;
    }

    public int findUserId(String name) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("SELECT \"UserID\" from \"Users\" where \"Name\" = ?; ");
        stmt.setString(1, name);
        int userId = getSingelInt(stmt, "UserID");
        return userId;
    }

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

    public String[] ShowFriends(int userId) throws SQLException {

        try {
            PreparedStatement stmt = connection.prepareStatement
                    ("SELECT \"Name\" FROM \"Users\" inner join \"FriendList\" on \"Users\".\"UserID\" = \"FriendList\".\"FriendID\"\n" +
                            "where \"Owner\" = ? ;");

            stmt.setInt(1, userId);
            ResultSet validUser = stmt.executeQuery();
            ArrayList<String> namelist = new ArrayList<String>();

            while ((validUser.next())) {
                String name = validUser.getString("Name");
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

    public void addRequests(int player1, int player2) {
        try {


            PreparedStatement stmt = connection.prepareStatement("insert into \"Request\"(\"Player1\" , \"Player2\" , \"Acceptance\") values(?,?,?)");
            stmt.setInt(1, player1);
            stmt.setInt(2, player2);
            stmt.setInt(3,0);

           int result = stmt.executeUpdate();
           connection.commit();



        } catch (Exception e){}
    }

    public String[]  findRequestsForPlayer(int playerId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("select \"Name\" from \"Users\" inner join \"Request\" on " +
                    "\"Request\".\"Player1\" = \"Users\".\"UserID\" where \"Player2\" = ? and \"Acceptance\" = ?");
            stmt.setInt(1, playerId);
            stmt.setInt(2, 0);
            String[] requestPlayer = getStringArray(stmt,"Name");
            return requestPlayer;
        } catch (Exception e) {
            return null;
        }
    }
    private String [] getStringArray(PreparedStatement stmt, String column){

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
    public void acceptGame(int player2, String challenger){
        try {
            int player1 = findUserId(challenger);

        }catch (Exception e){

        }
    }
    private void sendUpdate(PreparedStatement stmt){
        try {
            int result = stmt.executeUpdate();
        }catch (Exception e){

        }
    }




}
