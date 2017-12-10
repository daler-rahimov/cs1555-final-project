import java.sql.*;
import java.util.*;

public class Friends {

    public static boolean isTest = false;
    public static boolean isTest2 = false;

    /**
     * Create a pending friendship from the (logged in) user to another user based on userID. The
     application should display the name of the person that will be sent a friends request and the user
     should be prompted to enter a message to be sent along with the request. A last confirmation
     should be requested of the user before an entry is inserted into the pendingFriends relation,
     and success or failure feedback is displayed for the user.
     *
     * @param userID1
     */
    public static void initiateFriendship(String userID1){
        //make user input here
        String userID2, message;

        if(!isTest) {

            System.out.println("Please enter the ID of the user you would like to befriend");
            userID2 = UserInput.getID();

            String inputMessage = "Please enter a message you would like to send to this user: ";
            message = UserInput.getLine200(inputMessage);
        } else {
            userID2 = "15";
            message = "isTest initiateFriendship message";
        }

        try {
            ///// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            ///// 2. Check to make sure not friends by getting all of userID1's friends then looping through
            /////       to check that userID2 does not exist
            String selectSQL = "SELECT userID2\n"
                    + "FROM FRIENDS\n"
                    + "WHERE userID1 = ?";
            PreparedStatement prep = con.prepareStatement(selectSQL);
            prep.setString(1, userID1);
            ResultSet rs = prep.executeQuery();

            while(rs.next()){
                if(rs.getString(1).equals(userID2)) {
                    System.out.println("Friend is: " + rs.getString(1));
                    System.out.println("Already friends");
                    return;
                }
            }

            System.out.println("Are you sure you want to initiate a friendship with user " + userID2 + "?\n" +
                    + "Options: \n"
                    + "\t1. Yes\n"
                    + "\t2. No\n";);
            do{
                System.out.println("Options: ")
            }
            ///// 3. If not already friends, insert into pendingFriendships
            String insert = "INSERT INTO pendingFriends(fromID, toID, message)"
                    + "VALUES ("
                    + "?, "
                    + "?, "
                    + "?)";
            prep = con.prepareStatement(insert);
            prep.setString(1, userID1);
            prep.setString(2, userID2);
            prep.setString(3, message);
            prep.executeUpdate();

            prep.close();
            rs.close();

        } catch(SQLIntegrityConstraintViolationException Ex){
            System.out.println("You have already initiated a friendship with this user");
        } catch (SQLException Ex) {
            System.out.println("Friends >> Error: "
                    + Ex.toString());
        }
    }

    /**
     *This task should first display a formatted, numbered list of all outstanding friends and group
     requests with an associated messages. Then, the user should be prompted for a number of the
     request he or she would like to confirm or given the option to confirm them all. The application
     should move the request from the appropriate pendingFriends or pendingGroupmembers
     relation to the friends or groupMembership relation. The remaining requests which were not
     selected are declined and removed from pendingFriends and pendingGroupmembers relations.
     * @param userID1
     */
    public static void confirmFriendship(String userID1){
        try{
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            ///// 1. Find all friends request (where user is toUser in pending friendsgit
            /////   List all friends request and display message
            String selectSQL = "SELECT fromID, message\n"
                    + "FROM pendingFriends\n"
                    + "WHERE toID = ?";
            PreparedStatement prep = con.prepareStatement(selectSQL,  ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            prep.setString(1, userID1);
            ResultSet pendingFriends = prep.executeQuery();

            System.out.println("Here are all your friend requests: ");
            while(pendingFriends.next()){
                System.out.println("\tUser " + pendingFriends.getString(1)
                        + " : " + pendingFriends.getString(2));
            }

            pendingFriends.beforeFirst();

            //// 2. Check if user is manager of group
            ////    if yes, collect all groupRequests where the user is manager. list all requests and display messages
            selectSQL ="SELECT gID\n"
                    + "FROM groupMembership\n"
                    + "WHERE userID = ? AND role = 'manager'";
            prep = con.prepareStatement(selectSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            prep.setString(1, userID1);
            ResultSet groupsManaged = prep.executeQuery();

            Set<String> members = new HashSet<String>();
            Map<String, Set<String>> groupRequests = new HashMap<String, Set<String>>();

            while(groupsManaged.next()){
                selectSQL = "SELECT userID, message\n"
                        + "FROM pendingGroupmembers\n"
                        + "WHERE gID = ?";
                prep = con.prepareStatement(selectSQL);
                prep.setString(1, groupsManaged.getString(1));
                ResultSet pendingGroups = prep.executeQuery();

                System.out.println("Here are all your group requests for group " + groupsManaged.getString(1) + " : ");
                while(pendingGroups.next()){
                    members.add(pendingGroups.getString(1));
                    System.out.println("\tUser " + pendingGroups.getString(1)
                            + " : " + pendingGroups.getString(2));
                }
                groupRequests.put(groupsManaged.getString(1), members);
                members.clear();
                pendingGroups.close();
            }

            int selectionChoice;
            //// 3. Ask user if they want to confirm all or select confirmations
            do {
                String message = "Would you like to confirm all or manually select confirmations?\n"
                        + "Options: \n"
                        + "\t1. Select all\n"
                        + "\t2. Manually select\n"
                        + "\t3. Delete all requests\n";
                if(!isTest && !isTest2){
                    selectionChoice = UserInput.getInt(message);
                } else if (isTest){
                    selectionChoice = 1;
                } else {
                    selectionChoice = 2;
                }

            }while(selectionChoice <= 1 && selectionChoice >= 3);

            //// 4. If confirm all, send all requests to correct tables
            if(selectionChoice == 1){
                //insert all friends
                while(pendingFriends.next()){
                    //one direction
                    try {
                        String insertSQL = "INSERT INTO friends(userID1, userID2, JDate, message) VALUES(?, ?, ?, ?)";
                        prep = con.prepareStatement(insertSQL);
                        prep.setString(1, userID1);
                        prep.setString(2, pendingFriends.getString(1));
                        prep.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
                        prep.setString(4, pendingFriends.getString(2));
                        prep.executeUpdate();

                        //second direction
                        insertSQL = "INSERT INTO friends(userID1, userID2, JDate, message) VALUES(?, ?, ?, ?)";
                        prep = con.prepareStatement(insertSQL);
                        prep.setString(1, pendingFriends.getString(1));
                        prep.setString(2, userID1);
                        prep.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
                        prep.setString(4, pendingFriends.getString(2));
                        prep.executeUpdate();
                        prep.close();
                    } catch (SQLIntegrityConstraintViolationException Ex){
                        System.out.println("You already friends with someone selected.");
                    }
                }

                String curGroup = null;

                //insert all groupmembers
                Iterator<String> group = groupRequests.keySet().iterator();
                while(group.hasNext()){
                    curGroup = group.next();
                    members = groupRequests.get(curGroup);
                    Iterator<String> mem = members.iterator();
                    while(mem.hasNext()) {
                        try {
                            String insertSQl = "INSERT INTO groupMembership(gID, userID) VALUES(?, ?)";
                            prep = con.prepareStatement(insertSQl);
                            prep.setString(1, curGroup);
                            prep.setString(2, mem.next());
                            prep.executeUpdate();
                            prep.close();
                        } catch (SQLIntegrityConstraintViolationException Ex){
                            System.out.println("Someone selected is already a groupmember.");
                        }
                    }
                }
            }

            //// 5. If select, have a loop that will ask user to select the the number of confirmations
            ////    then ask user to select which requests to confirm
            ////    Potential corner: if user selects more than initially indicated, prompt user if they want to select
            ////    more than initially indicated? Or just make array whatever size of input??
            ////    Remove any accepted requests
            ////    Potentially break this down for
            else if(selectionChoice == 2) {
                int count = 0;
                int select = 0;
                do {
                    String message = "Would you like to confirm a friend or group member?\n"
                            + "Options: \n"
                            + "\t1. Friend\n"
                            + "\t2. Groupmember\n"
                            + "\t3. Exit\n";
                    do {
                        if(!isTest && !isTest2) {
                            select = UserInput.getInt(message);
                        } else if(count > 1){
                            select = 3;
                        }
                        else {
                            count++;
                            select = 2;
                        }
                    } while (select <= 1 && select >= 3);

                    if (select == 1) {
                        System.out.println("Please select userID to confirm friendship: ");
                        String userID2 = UserInput.getID();

                        //check if input is valid
                        Boolean canAdd = false;
                        pendingFriends.beforeFirst();
                        while(pendingFriends.next()){
                            if(pendingFriends.getString(1).equals(userID2))
                                canAdd = true;
                        }

                        if (canAdd) {
                            //look through pending friends
                            try {
                                String insertSQL = "INSERT INTO friends(userID1, userID2, JDate, message) VALUES(?, ?, ?, ?)";
                                prep = con.prepareStatement(insertSQL);
                                prep.setString(1, userID1);
                                prep.setString(2, userID2);
                                prep.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
                                prep.setString(4, pendingFriends.getString(2));
                                prep.executeUpdate();

                                //second direction
                                insertSQL = "INSERT INTO friends(userID1, userID2, JDate, message) VALUES(?, ?, ?, ?)";
                                prep = con.prepareStatement(insertSQL);
                                prep.setString(1, pendingFriends.getString(1));
                                prep.setString(2, userID1);
                                prep.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
                                prep.setString(4, pendingFriends.getString(2));
                                prep.executeUpdate();
                                prep.close();
                            } catch (SQLIntegrityConstraintViolationException Ex){
                                System.out.println("You already friends with this person.");
                            }
                        } else {
                            System.out.println("Not a valid userID. Please select again.");
                        }
                    } else if (select == 2) {
                        System.out.println("Please select groupID you want to confirm for: ");
                        String gID;
                        if(!isTest && !isTest2){
                            gID = UserInput.getID();
                        } else {
                            gID = "9";
                        }

                        //confirm that this person is manager
                        //look through managed groups
                        Boolean isManager = false;
                        groupsManaged.beforeFirst();
                        while(groupsManaged.next()){
                            if(groupsManaged.getString(1).equals(gID))
                                isManager = true;
                        }

                        if(isManager) {
                            System.out.println("Please select userID of new group member: ");
                            String userID2;
                            if(!isTest && !isTest2) {
                                userID2 = UserInput.getID();
                            } else {
                                userID2 = "99";
                            }

                            //confirm that this person has sent in a request
                            //look through group for that grouprequests
                            Boolean didRequest = false;
                            Iterator<String> it = members.iterator();
                            while(it.hasNext()){
                                if(it.next().equals(userID2))
                                    didRequest = true;
                            }

                            if(didRequest) {
                                try {
                                    String insertSQl = "INSERT INTO groupMembership(gID, userID) VALUES(?, ?)";
                                    prep = con.prepareStatement(insertSQl);
                                    prep.setString(1, gID);
                                    prep.setString(2, userID2);
                                    prep.executeUpdate();
                                    prep.close();
                                } catch (SQLIntegrityConstraintViolationException Ex){
                                    System.out.println("This person is already in the group.");
                                }
                            } else {
                                System.out.println("This person did not request to join the group.");
                            }
                        } else {
                            System.out.println("You are not a manager of this group. Please select another group.");
                        }
                    }
                }while(select != 3);
            }

            //// 6. Any requests that were not accepted are now deleted
            String delete = "DELETE FROM pendingFriends\n"
                    + "WHERE toID = ?";
            prep = con.prepareStatement(delete);
            prep.setString(1, userID1);
            prep.executeUpdate();

            groupsManaged.beforeFirst();
            while(groupsManaged.next()){
                delete = "DELETE FROM pendingGroupmembers\n"
                        + "WHERE gID = ?";
                prep = con.prepareStatement(delete);
                prep.setString(1, groupsManaged.getString(1));
                prep.executeUpdate();
            }

            prep.close();
            groupsManaged.close();
            pendingFriends.close();

        } catch (SQLException Ex){
            System.out.println("Friends >> Error: "
                    + Ex.toString());
        }

        return;
    }

    /**
     * This task supports the browsing of the users friends and of their friends profiles. It first
     displays each of the users friends names and userIDs and those of any friend of those friends.
     Then it allows the user to either retrieve a friends entire profile by entering the appropriate
     userID or exit browsing and return to the main menu by entering 0 as a userID. When selected,
     a friends profile should be displayed in a nicely formatted way, after which the user should be
     prompted to either select to retrieve another friends profile or return to the main menu.
     *
     */
    public static void displayFriends(String userID1){
        try{
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            Set<String> toDisplay = new HashSet<String>();

            ///// 1. List the userIDs of all friends of userID1 and the friends' friends (excluding userID1)
            String selectSQL = "SELECT userID2\n"
                    + "FROM FRIENDS\n"
                    + "WHERE userID1 = ?\n";
            PreparedStatement prep = con.prepareStatement(selectSQL);
            prep.setString(1, userID1);
            //prep.setString(2, userID1);
            ResultSet userFriends = prep.executeQuery();

            Boolean friends = false;

            while(userFriends.next()){
                friends = true;
                toDisplay.add(userFriends.getString(1));
                selectSQL = "SELECT userID2\n"
                        + "FROM FRIENDS\n"
                        + "WHERE userID1 = ?";
                prep = con.prepareStatement(selectSQL);
                prep.setString(1, userFriends.getString(1));
                //prep.setString(2, userID1);
                ResultSet friendFriends = prep.executeQuery();

                while(friendFriends.next()){
                    toDisplay.add(friendFriends.getString(1));
                }

                friendFriends.close();
            }

            userFriends.close();

            if(!friends){
                System.out.println("There are no profiles to display.");
                prep.close();
                con.close();
                return;
            }

            //create iterator to display all the choices
            Iterator<String> it = toDisplay.iterator();
            System.out.println("Here is a list of IDs that are your friends and their friends: ");
            while(it.hasNext()){
                System.out.println("\t" + it.next());
            }

            //// 2. Ask userID1 to input an userID
            //// if 0, return back
            //// for anyother userID, retrieve profile information (excluding password)
            //// prompt user if they want to look at more profiles
            String message = "Would you like to look up a profile?\n"
                    + "Options: \n"
                    + "1: Yes\n"
                    + "2: No \n";
            int keepLooking;
            if(!isTest) {
                keepLooking = UserInput.getInt(message);
            } else {
                keepLooking = 1;
            }

            while(keepLooking == 1) {
                System.out.println("Please enter the ID of the user you want to look at: ");

                String userID2;
                if(!isTest) {
                    userID2 = UserInput.getID();
                } else {
                    userID2 = "94";
                }
                if(toDisplay.contains(userID2)){
                    selectSQL = "SELECT userID, name, email, date_of_birth, lastLogin\n"
                            + "FROM PROFILE\n"
                            + "WHERE userID = ?";
                    prep = con.prepareStatement(selectSQL);
                    prep.setString(1, userID2);
                    ResultSet rs = prep.executeQuery();

                    rs.next();
                    System.out.println("\tUserID: " + userID2
                            + "\n\tName: " + rs.getString(2)
                            + "\n\tEmail: " + rs.getString(3)
                            + "\n\tDate of Birth: " + rs.getDate(4)
                            + "\n\tLast Login: " + rs.getDate(5));

                    message = "Would you like to look up another profile?\n"
                            + "Options: \n"
                            + "\t1: Yes\n"
                            + "\t2: No \n";
                    if(!isTest) {
                        keepLooking = UserInput.getInt(message);
                    } else{
                        keepLooking = 2;
                    }

                    rs.close();
                } else{
                    System.out.println("This user if not accessible.");
                    message = "Would you like to try a different user?\n"
                            + "Options: \n"
                            + "1: Yes\n"
                            + "2: No \n";
                    if(!isTest) {
                        keepLooking = UserInput.getInt(message);
                    } else {
                        keepLooking = 2;
                    }
                }
            }

            prep.close();
            con.close();
        } catch (SQLException Ex){
            System.out.println("Friends >> Error: "
                    + Ex.toString());
        }


    }
}
