
import static java.lang.System.exit;
import java.sql.*;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Profile {

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// use java.sql classes instead 

    /**
     * Given a name, email address, and date of birth, add a new user to the
     * system by inserting as new entry in the profile relation
     */
    public static void createUser(String name, String email, java.sql.Date birthdate, String password) {
        //requirements for creating a profile
        //userID will be created by finding the next available number for the ID
        //last login will be added at this time

        try {
            ////// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            ///// 2. Find next available userI
            String selectSQL = "select max(to_number(regexp_substr(userid, '\\d+')))+1 userid from PROFILE"; // notice that "//" is escape and in sqlplues its "/"
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(selectSQL);
            int nextUserID = 0;
            if (rs.next()) {
                nextUserID = rs.getInt(1);
            }else {
                System.err.print("Profile>createUser() Error getting nextMsgID");
                exit(1);
            }
            
            // create a profile instance Profile(String userID, String name, String password, java.sql.Date dateOfBirth, Timestamp lastlogin, String email)
            Profile p = new Profile(Integer.toString(nextUserID), name, password, birthdate, new Timestamp(new java.util.Date().getTime()), email);
            p.insertToDb(con);
            
            
//            int userID;
//            String idQuery = "";

//            Statement stmt = con.createStatement();
//            PreparedStatement preparedStatement = con.prepareStatement(idQuery);
//            ResultSet rs = preparedStatement.executeQuery();

            ///// 3. Insert into database
//            String insertQuery = "INSERT INTO PROFILE"
//                    + "(userid, name, password, date_of_birth, lastlogin)" + "VALUES"
//                    + "('" + userID + "', '" + name + "', '" + email + "', to_date('" + birthdate
//                    + ", 'DD-MON-YY'), to_timestamp('" + getCurrentTimeStamp() + "', 'yyyy/mm/dd hh24:mi:ss'))";

        } catch (SQLException Ex) {
            System.out.println("Message >> Error: " + Ex.toString());
        }
    }

    /**
     * Remove a user and all of their information from the system. When a user
     * is removed, the system should delete the user from the groups he or she
     * was a member of using a trigger. Note: messages require special handling
     * because they are owned by both sender and receiver. Therefore, a message
     * is deleted only when both he sender and all receivers are deleted.
     * Attention should be paid handling integrity constraints.
     *
     */
    public static void dropUser() {

    }

    /**
     * Given two users (A and B), find a path, if one exists, between A and B
     * with at most 3 hop between them. A hop is defined as a friendship between
     * any two users
     */
    public static void threeDegrees() {

    }

    /**
     * Given a string on which to match any user in the system, any item in this
     * string must be matched against any significant field of a user’s profile.
     * That is if the user searches for “xyz abc”, the results should be the set
     * of all profiles that match “xyz” union the set of all profiles that
     * matches “abc”
     */
    public static void searchForUser() {

    }

    /**
     * Given userID and password, login as the user in the system when an
     * appropriate match is found.
     */
    public static void login() {

    }

    /**
     * This option should cleanly shut down and exit the program after marking
     * the time of the user’s logout in the profile relation,
     */
    public static void logout() {

    }

    private static String getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return dateFormat.format(today.getTime());

    }

    private String userID;
    private String name;
    private String password;
    private java.sql.Date dateOfBirth;
    private java.sql.Timestamp lastlogin;
    private String email;
    /**
     * 
     * @param con
     * @return 0 success and >0 fail  
     */
    public int insertToDb(Connection con) {
        //INSERT INTO profile (userid,name,password,date_of_birth,lastlogin) VALUES ('8','Brynne','WRG10AGK0MZ','05-Sep-96','30-Nov-17');
        String insertSQL = "INSERT INTO profile (userid,name,password,date_of_birth,lastlogin) "
                + "VALUES ("
                + " ?"
                + ",?"
                + ",?"
                + ",?"
                + ",?)";
        try {
            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(insertSQL);

            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, password);
            preparedStatement.setDate(4, dateOfBirth);
            preparedStatement.setTimestamp(5, lastlogin);

            ResultSet rs = preparedStatement.executeQuery();
        } catch (SQLException Ex) {
            System.err.println("Profile>insertToDb() >> Error: "
                    + Ex.toString());
            return 1;
        }
        return 0;
    }

    public Profile(String userID, String name, String password, java.sql.Date dateOfBirth, Timestamp lastlogin, String email) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.lastlogin = lastlogin;
        this.email = email;
    }

    public Profile(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public Profile() {
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public java.sql.Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Timestamp getLastlogin() {
        return lastlogin;
    }

    public String getEmail() {
        return email;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateOfBirth(java.sql.Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setLastlogin(Timestamp lastlogin) {
        this.lastlogin = lastlogin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
