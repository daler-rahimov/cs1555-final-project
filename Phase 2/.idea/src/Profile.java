import java.sql.*;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Profile {
    private static Connection connection; //used to hold the jdbc connection to the DB
    private Statement statement; //used to create an instance of the connection
    private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private ResultSet resultSet; //used to hold the result of your query (if one exists)
    private String query;  //this will hold the query we are using
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Given a name, email address, and date of birth, add a new user to the system by inserting as
     new entry in the profile relation
     */
    public void createUser(String name, String email, String birthdate, String password){
        //requirements for creating a profile
        //userID will be created by finding the next available number for the ID
        //last login will be added at this time

        int userID;
        //need to create query to get next available ID

        try {
            connection.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();

            query = "insert into profile" +
                    "(userid, name, password, date_of_birth, lastlogin)" +  "values"
                    + "('" + userID + "', '" + name + "', '" + email + "', to_date('" + birthdate +
                    ", 'DD-MON-YY'), to_timestamp('" + getCurrentTimeStamp() + "', 'yyyy/mm/dd hh24:mi:ss'))";

        }


    };

    /**
     * Remove a user and all of their information from the system. When a user is removed, the system
     should delete the user from the groups he or she was a member of using a trigger. Note:
     messages require special handling because they are owned by both sender and receiver. Therefore,
     a message is deleted only when both he sender and all receivers are deleted. Attention
     should be paid handling integrity constraints.

     */
    public void dropUser(){

    };

    /**
     * Given two users (A and B), find a path, if one exists, between A and B with at most 3 hop
     between them. A hop is defined as a friendship between any two users
     */
    public void threeDegrees(){

    };

    /**
     *Given a string on which to match any user in the system, any item in this string must be
     matched against any significant field of a user’s profile. That is if the user searches for “xyz
     abc”, the results should be the set of all profiles that match “xyz” union the set of all profiles
     that matches “abc”
     */
    public void searchForUser(){

    };

    /**
     * Given userID and password, login as the user in the system when an appropriate match is
     found.
     */
    public void login(){

    };

    /**
     * This option should cleanly shut down and exit the program after marking the time of the user’s
     logout in the profile relation,
     */
    public void logout(){

    };

    private static String getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return dateFormat.format(today.getTime());

    }
}
