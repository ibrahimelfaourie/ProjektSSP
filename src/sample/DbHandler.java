package sample;

import java.sql.*;

public class DbHandler {

    Connection connection = null;
    Statement processSqlStatement = null;

    public void initConection(){

        // Statement processSqlStatement = null;
        try {

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Spelet", "ibbe", "123456");

            connection.setAutoCommit(false);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public int login(String username, String password){
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) AS total FROM \"users\"" +
                    " where \"username\" = ? and \"password\" = ?;");
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            // ResultSet validUser = stmt.executeQuery();
            int antal = -1;
            if ( rs.next() ) {
                antal = rs.getInt("total");
            }
            return antal;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return 0;

        }

    }

    public void ShowFriends(int userId){

        try {
            PreparedStatement stmt = connection.prepareStatement
                    ("SELECT friendwith FROM \"friends\" where \"owner\" = ? ;");
            stmt.setInt(1, userId);
            ResultSet validUser = stmt.executeQuery();
        } catch (Exception e){

        }
    }
}
