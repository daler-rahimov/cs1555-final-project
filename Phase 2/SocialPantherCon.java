/*
 * This class will have all the credentials for the Oracle database and returns 
 * a same connection every time when its instantiated. 
 * 
 * Usage Example: 
 * 
        try {

            SocialPantherCon sCon = new SocialPantherCon();
            
            Connection con = sCon.getConnection();
            Statement stmt = con.createStatement();
            //step4 execute query
            ResultSet rs = stmt.executeQuery("select * from groupMembership");
            
            while (rs.next()) {
                System.out.println(rs.getString("gID") + "  " + rs.getString("userID") + "  " + rs.getString("role"));
            }
            con.close();
            stmt.close();
            rs.close();
        } catch (SQLException Ex) {
            System.out.println("TestOJDBS2 >> Error: "
                    + Ex.toString());
        } 
 */

/**
 *
 * @author Daler
 */
import java.sql.*;

public class SocialPantherCon {

    public static Connection connection; //used to hold the jdbc connection to the DB
    
    public Connection getConnection(){
        return connection;
    }
    public SocialPantherCon() throws SQLException {
        if (!(connection instanceof Connection)) {
            String username, password;
            username = "mel118"; //This is your username in oracle
            password = "psswrd!"; //This is your password in oracle
            try {
                // Register the oracle driver.  
                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
               // String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
                
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException Ex) {
                System.out.println("SocialPantherCcon >> Error connecting to database.  Machine Error: "
                        + Ex.toString());
            }
        }
    }
}
