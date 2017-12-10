import static java.lang.System.exit;
import java.sql.*;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Profile {

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// use java.sql classes instead
    public static boolean isTest = false;

    /**
     * Given a name, email address, and date of birth, add a new user to the
     * system by inserting as new entry in the profile relation
     */
    public static void createUser() {
        //requirements for creating a profile
        //userID will be created by finding the next available number for the ID
        //last login will be added at this time
        String name, email, date, password;

        if(!isTest) {
            System.out.println("Please enter your name: ");
            name = UserInput.getUserName();

            System.out.println("Please enter your email: ");
            email = UserInput.getEmail();

            System.out.println("Please enter your birthday (dd-mm-yyyy): ");
            date = UserInput.getBDay();

            System.out.println("Please enter a password: ");
            password = UserInput.getPassword();
        } else {
            name = "Gabby";
            email = "Gabby@pitt.edu";
            date = "12-09-1982";
            password = "KJGD896JK";
        }

        java.util.Date utilDate = null;
        try{
            utilDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch(ParseException e){
            System.out.println("createUser date parsing error: " + e.toString());
        }

        java.sql.Date birthdate = new java.sql.Date(utilDate.getTime());

        try {
            ////// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            String lock = "lock table Profile in exclusive mode";

            ///// 2. Find next available userID
            String selectSQL = "select max(to_number(regexp_substr(userid, '\\d+')))+1 userid from PROFILE"; //// notice that "//" is escape and in sqlplues its "/"
            PreparedStatement prep = con.prepareStatement(selectSQL);
            prep.execute(lock);
            ResultSet rs = prep.executeQuery(selectSQL);
            String nextUserID = null;
            if (rs.next()) {
                nextUserID = rs.getString(1);
            }else {
                System.err.print("Profile>createUser() Error getting nextMsgID");
                exit(1);
            }

            String insertSQL = "INSERT" +
                    " INTO profile (userid,name,password,date_of_birth, email) "
                    + "VALUES ("
                    + " ?"
                    + ",?"
                    + ",?"
                    + ",?"
                    + ",?)";

            prep = con.prepareStatement(insertSQL);

            prep.setString(1, nextUserID);
            prep.setString(2, name);
            prep.setString(3, password);
            prep.setDate(4, birthdate);
            prep.setString(5, email);
            prep.executeUpdate();

            // create a profile instance Profile(String userID, String name, String password, java.sql.Date dateOfBirth, Timestamp lastlogin, String email)
            //Profile p = new Profile(Integer.toString(nextUserID), name, password, birthdate, new Timestamp(new java.util.Date().getTime()), email);
            con.commit();
            // p.insertToDb(con);

            rs.close();

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
    public static void dropUser(String userID) {
        try {

            ///// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            String lock = "lock table Profile in exclusive mode";

            ///// 2. Delete
            String delete = "delete from friends where userID1 = ? or userID2 = ?";
            PreparedStatement prep = con.prepareStatement(delete);
            prep.setString(1, userID);
            prep.setString(2, userID);
            prep.execute(lock);
            prep.executeUpdate();
//            System.out.println("all deleted. ");

            delete = "delete from pendingGroupmembers where userID = ?";
            prep = con.prepareStatement(delete);
            prep.setString(1, userID);
            prep.executeUpdate();
//            System.out.println("all deleted. ");

            delete = "delete from pendingFriends where fromID = ? or toID = ?";
            prep = con.prepareStatement(delete);
            prep.setString(1, userID);
            prep.setString(2, userID);
            prep.executeUpdate();
//            System.out.println("all deleted. ");

            delete = "delete from groupMembership where userID = ?";
            prep = con.prepareStatement(delete);
            prep.setString(1, userID);
            prep.executeUpdate();
//            System.out.println("all deleted. ");

            delete = "delete from messageRecipient where msgID IN (\n"
                    + "    select msgID from messages where  (fromID = ?) or (toUserID = ? )\n"
                    + "    )";
            prep = con.prepareStatement(delete);
            prep.setString(1, userID);
            prep.setString(2, userID);
            prep.executeUpdate();
//            System.out.println("all deleted. ");

            delete = "delete from messages where (fromID = ? and toUserID = null) or (toUserID = ? and fromID = null)";
            prep = con.prepareStatement(delete);
            prep.setString(1, userID);
            prep.setString(2, userID);
            prep.executeUpdate();
//            System.out.println("all deleted. ");

            delete = "DELETE FROM profile WHERE userid = ?";
            prep = con.prepareStatement(delete);
            prep.setString(1, userID);
            int count = prep.executeUpdate();

            con.commit();

            if(count == 0){
                System.out.println("User does not exist");
            }else{
                System.out.println("User and all data related is deleted. ");
            }

            prep.close();

        } catch (SQLException Ex) {
            System.out.println("Message >> Error: " + Ex.toString());
        }
    }

    /**
     * Given two users (A and B), find a path, if one exists, between A and B
     * with at most 3 hop between them. A hop is defined as a friendship between
     * any two users
     */
    public static void threeDegrees() {
        String userID1, userID2;


        //potentially add multiple flags to show that each type of path works
        if(!isTest) {
            System.out.print("Please enter first ID: ");
            userID1 = UserInput.getID();

            System.out.print("Please enter second ID: ");
            userID2 = UserInput.getID();
        } else {
            userID1 = "43";
            userID2 = "48";
        }

        LinkedHashSet<String> threeDegreesPath = new LinkedHashSet<String>();
        try{
            ///// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            threeDegreesPath.add(userID1);
            Boolean pathFound = false;

            ///// 2. Look through userID1 friends list (hop 1) for userID2
            String selectSQL = "SELECT userID2\n"
                    + "FROM friends\n"
                    + "WHERE userID1 = ?";
            PreparedStatement prep = con.prepareStatement(selectSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            prep.setString(1, userID1);
            ResultSet firstHop = prep.executeQuery();

            while(firstHop.next()){
                //System.out.println(firstHop.getString(1));
                if(firstHop.getString(1).equals(userID2)) {
                    threeDegreesPath.add(userID2);
                    pathFound = true;
                    break;
                }
            }

            if(pathFound){
                Iterator<String> it = threeDegreesPath.iterator();
                System.out.println("The path between user " + userID1 + " and user " + userID2 + " is: ");
                while(it.hasNext()){
                    System.out.println("\t" + it.next());
                }

                prep.close();
                firstHop.close();
                return;
            }

            ///// 3. Go through all of userID1 friends' friends list (hop 2) for userID2
            //return cursor back to first pointer
            firstHop.beforeFirst();
            Map<String, String> secondSet = new HashMap<String, String>();

            while(firstHop.next()) {
                String userID3 = firstHop.getString(1);
                selectSQL = "SELECT userID2\n"
                        + "FROM friends\n"
                        + "WHERE userID1 = ?";
                prep = con.prepareStatement(selectSQL);
                prep.setString(1, userID3);
                ResultSet secondHop = prep.executeQuery();

                while (secondHop.next()) {
                    if (secondHop.getString(1).equals(userID2)) {
                        threeDegreesPath.add(userID3);
                        threeDegreesPath.add(userID2);
                        pathFound = true;
                        break;
                    }else{
                        secondSet.put(secondHop.getString(1), userID3);
                    }
                }
                secondHop.close();
            }

            if(pathFound){
                Iterator<String> it = threeDegreesPath.iterator();
                System.out.println("The path between user " + userID1 + " and user " + userID2 + " is: ");
                while(it.hasNext()){
                    System.out.println("\t" + it.next());
                }

                prep.close();
                firstHop.close();
                return;
            }

            ///// 4. Go through all of userID1 friends friends friend list (hop 3) for userID2
            Iterator<String> it = secondSet.keySet().iterator();
            while(it.hasNext()){
                String userID3 = it.next();
                selectSQL = "SELECT userID2\n"
                        + "FROM friends\n"
                        + "WHERE userID1 = ?";
                prep = con.prepareStatement(selectSQL);
                prep.setString(1, userID3);
                ResultSet thirdHop = prep.executeQuery();

                while (thirdHop.next()) {
                    if (thirdHop.getString(1).equals(userID2)) {
                        threeDegreesPath.add(secondSet.get(userID3));
                        threeDegreesPath.add(userID3);
                        threeDegreesPath.add(userID2);
                    }
                }
                thirdHop.close();
            }

            prep.close();
            firstHop.close();

        } catch (SQLException Ex) {
            System.out.println("Message >> Error: " + Ex.toString());
        }

        if(threeDegreesPath.contains(userID2)){
            Iterator<String> it = threeDegreesPath.iterator();
            System.out.println("The path between user " + userID1 + " and user " + userID2 + " is: ");
            while(it.hasNext()){
                System.out.println("\t" + it.next());
            }
        }
        else{
            System.out.println("There is not path between user " + userID1 + " and user " + userID2);
        }
    }

    /**
     * Given a string on which to match any user in the system, any item in this
     * string must be matched against any significant field of a users profile.
     * That is if the user searches for xyz abc, the results should be the set
     * of all profiles that match xyz union the set of all profiles that
     * matches abc
     */
    public static void searchForUser() {
        try{
            ///// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            ///// 2. Get string and parse string for each keyword added to search

            String[] searches;

            if(!isTest) {
                String message = "Please type search keywords: ";
                searches = UserInput.getSearch(message);
            } else {
                searches = new String[] {"12", "Bob", "Gabby"};
            }

            ///// 3. Perform search in userID, name, and email for each keyword
            Set<Profile> matches = new HashSet<Profile>();
            String check;

            for(int i = 0; i < searches.length; i++){
                check = searches[i];
                String selectSQL = "SELECT userID, name\n"
                        + "FROM profile\n"
                        + "WHERE userID LIKE ? OR name LIKE ? OR email LIKE ?";
                PreparedStatement prep = con.prepareStatement(selectSQL);
                prep.setString(1, "%" + check + "%");
                prep.setString(2, "%" + check + "%");
                prep.setString(3, "%" + check + "%");
                ResultSet rs = prep.executeQuery();

                while(rs.next()){
                    //System.out.println(rs.getString(1));
                    //System.out.println("\t" + rs.getString(2));
                    matches.add(new Profile(rs.getString(1), rs.getString(2)));
                }
            }

            System.out.println("The matches are: ");
            Iterator<Profile> allMatches = matches.iterator();
            while(allMatches.hasNext()){
                Profile p = allMatches.next();
                System.out.println("userID: " + p.getUserID());
                System.out.println("\tName: " + p.getName());
            }


        } catch (SQLException Ex) {
            System.out.println("Message >> Error: " + Ex.toString());
        }
    }

    /**
     * Given userID and password, login as the user in the system when an
     * appropriate match is found.
     */
    public static Boolean login() {
        String userID, password;

        if(!isTest) {
            System.out.print("Please enter your userID: ");
            userID = UserInput.getID();

            String message = "Please enter your passord: ";
            password = UserInput.getLine20(message);
        } else {
            userID = "1";
            password = "TMA58URO6JG";
        }

        Boolean loggedIn = false;
        try{
            ///// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            ///// 2. Search for a match to userID and password
            String selectSQL = "SELECT userID\n"
                    + "FROM PROFILE\n"
                    + "WHERE userID = ? AND password = ?";
            PreparedStatement prep = con.prepareStatement(selectSQL);
            prep.setString(1, userID);
            prep.setString(2, password);
            ResultSet rs = prep.executeQuery();

            ///// 3. Check to make sure that the user is returned and login if they are a user
            if(rs.next()){
                if(rs.getString(1).equals(userID))
                    loggedIn = true;
            }
            else {
                System.err.print("User login or password is incorrect");
                return false;
            }

            prep.close();
            rs.close();
        } catch (SQLException Ex) {
            System.out.println("Message >> Error: " + Ex.toString());
        }

        return loggedIn;
    }

    /**
     * This option should cleanly shut down and exit the program after marking
     * the time of the users logout in the profile relation,
     */
    public static void logout(String userID) {
        try{
            ///// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            String lock = "lock table Profile in exclusive mode";

            ///// 2. Add current time to last logged in time
            String update = "UPDATE profile "
                    + "Set lastLogin = ?"
                    + "WHERE userid = ?";
            PreparedStatement prep = con.prepareStatement(update);
            prep.setTimestamp(1, getCurrentTimeStamp());
            prep.setString(2, userID);
            prep.execute(lock);
            prep.executeUpdate();
            con.commit();

            prep.close();

        } catch (SQLException Ex) {
            System.out.println("Message >> Error: " + Ex.toString());
        }
    }

    private static java.sql.Timestamp getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

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
   /* public int insertToDb(Connection con) {
        //INSERT INTO profile (userid,name,password,date_of_birth,lastlogin) VALUES ('8','Brynne','WRG10AGK0MZ','05-Sep-96','30-Nov-17');
        String insertSQL = "INSER" +
                "T INTO profile (userid,name,password,date_of_birth,lastlogin, email) "
                + "VALUES ("
                + " ?"
                + ",?"
                + ",?"
                + ",?"
                + ",?"
                + ",?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(insertSQL);

            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, password);
            preparedStatement.setDate(4, dateOfBirth);
            preparedStatement.setTimestamp(5, lastlogin);
            preparedStatement.setString(6, email);

            preparedStatement.executeUpdate();
        } catch (SQLException Ex) {
            System.err.println("Profile>insertToDb() >> Error: "
                    + Ex.toString());
            return 1;
        }
        return 0;
    }*/

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
