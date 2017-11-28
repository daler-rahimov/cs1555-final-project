import java.sql.*;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Profile {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Given a name, email address, and date of birth, add a new user to the system by inserting as
     new entry in the profile relation
     */
    public static void createUser(String name, String email, String birthdate, String password){
        //requirements for creating a profile
        //userID will be created by finding the next available number for the ID
        //last login will be added at this time

        try {
            ////// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            ///// 2. Find next available userI
            int userID;
            String idQuery = "";

            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(idQuery);
            ResultSet rs = preparedStatement.executeQuery();



            ///// 3. Insert into database

            String insertQuery = "INSERT INTO PROFILE" +
                    "(userid, name, password, date_of_birth, lastlogin)" +  "VALUES"
                    + "('" + userID + "', '" + name + "', '" + email + "', to_date('" + birthdate +
                    ", 'DD-MON-YY'), to_timestamp('" + getCurrentTimeStamp() + "', 'yyyy/mm/dd hh24:mi:ss'))";

        }
        catch (SQLException Ex) {
            System.out.println("Message >> Error: " + Ex.toString());
        }
    };

    /**
     * Remove a user and all of their information from the system. When a user is removed, the system
     should delete the user from the groups he or she was a member of using a trigger. Note:
     messages require special handling because they are owned by both sender and receiver. Therefore,
     a message is deleted only when both he sender and all receivers are deleted. Attention
     should be paid handling integrity constraints.

     */
    public static void dropUser(){

    };

    /**
     * Given two users (A and B), find a path, if one exists, between A and B with at most 3 hop
     between them. A hop is defined as a friendship between any two users
     */
    public static void threeDegrees(){

    };

    /**
     *Given a string on which to match any user in the system, any item in this string must be
     matched against any significant field of a user’s profile. That is if the user searches for “xyz
     abc”, the results should be the set of all profiles that match “xyz” union the set of all profiles
     that matches “abc”
     */
    public static void searchForUser(){

    };

    /**
     * Given userID and password, login as the user in the system when an appropriate match is
     found.
     */
    public static void login(){

    };

    /**
     * This option should cleanly shut down and exit the program after marking the time of the user’s
     logout in the profile relation,
     */
    public static void logout(){

    };

    private static String getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return dateFormat.format(today.getTime());

    }
}
