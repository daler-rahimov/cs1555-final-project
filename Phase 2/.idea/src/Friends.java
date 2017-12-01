import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;

public class Friends {

    /**
     * Create a pending friendship from the (logged in) user to another user based on userID. The
     application should display the name of the person that will be sent a friends request and the user
     should be prompted to enter a message to be sent along with the request. A last confirmation
     should be requested of the user before an entry is inserted into the pendingFriends relation,
     and success or failure feedback is displayed for the user.
     *
     * @param userID1
     * @param userID2
     */
    public static Boolean initiateFriendship(String userID1, String userID2, String message){
        try {
            ///// 1. Connect to database
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            ///// 2. Check to make sure not friends by getting all of userID1's friends then looping through
            /////       to check that userID2 does not exist
            String selectSQL = "SELECT userID\n"
                    + "FROM FRIENDS\n"
                    + "WHERE userID1 = ? OR userID2 = ?";
            PreparedStatement prep = con.prepareStatement(selectSQL);
            prep.setString(1, userID1);
            prep.setString(1, userID1);
            ResultSet rs = prep.executeQuery();

            while(rs.next()){
                if(rs.getString(1).equals(userID2))
                    return false;
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

            con.close();
            prep.close();
            rs.close();

            return true;

        } catch (SQLException Ex) {
            System.out.println("Friends >> Error: "
                    + Ex.toString());
        }
        return false;
    }

    /**
     *This task should first display a formatted, numbered list of all outstanding friends and group
     requests with an associated messages. Then, the user should be prompted for a number of the
     request he or she would like to confirm or given the option to confirm them all. The application
     should move the request from the appropriate pendingFriends or pendingGroupmembers
     relation to the friends or groupMembership relation. The remaining requests which were not
     selected are declined and removed from pendingFriends and pendingGroupmembers relations.
     * @param userID1
     * @param userID2
     */
    public void confirmFriendship(String userID1){
        try{
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            ///// 1. Find all friends request (where user is toUser in pending friends
            /////   List all friends request and display message
            String selectSQL = "SELECT fromUserID, message\n"
                    + "FROM pendingFriends"
                    + "WHERE toUserID = ?";
            PreparedStatement prep = con.prepareStatement(selectSQL);
            prep.setString(1, userID1);
            ResultSet pendingFriends = prep.executeQuery();

            System.out.println("Here are all your friend requests: ");
            while(pendingFriends.next()){
                System.out.println(pendingFriends.getString(1)
                    + "\n\t" + pendingFriends.getString(2));
            }

            pendingFriends.first();

            //// 2. Check if user is manager of group
            ////    if yes, collect all groupRequests where the user is manager. list all requests and display messages
            selectSQL ="SELECT gID\n"
                    + "FROM groupMembership\n"
                    + "WHERE userID = ? AND role = 'manager'";
            prep = con.prepareStatement(selectSQL);
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
                    System.out.println(pendingGroups.getString(1)
                        + "\n\t" + pendingGroups.getString(2));
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
                        + "\t2. Manually select\n";
                selectionChoice = UserInput.getInt(message);
            }while(selectionChoice != 1 || selectionChoice !=2);

            //// 4. If confirm all, send all requests to correct tables
            if(selectionChoice == 1){
                //insert all friends
                while(pendingFriends.next()){
                    String insertSQL = "INSERT INTO friends(userID1, userID2, JDate, message) VALUES(?, ?, ?, ?)";
                    prep = con.prepareStatement(insertSQL);
                    prep.setString(1, userID1);
                    prep.setString(2, pendingFriends.getString(1));
                    prep.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
                    prep.setString(4, pendingFriends.getString(2));
                    prep.executeUpdate();
                    prep.close();
                }

                String curGroup = null;

                //insert all groupmembers
                Iterator<String> group = groupRequests.keySet().iterator();
                while(group.hasNext()){
                    curGroup = group.next();
                    members = groupRequests.get(curGroup);
                    Iterator<String> mem = members.iterator();
                    while(mem.hasNext()) {
                        String insertSQl = "INSERT INTO groupMembership(gID, userID) VALUES(?, ?)";
                        prep = con.prepareStatement(insertSQl);
                        prep.setString(1, curGroup);
                        prep.setString(2, mem.next());
                        prep.executeUpdate();
                        prep.close();
                    }
                }
            }

            //// 5. If select, have a loop that will ask user to select the the number of confirmations
            ////    then ask user to select which requests to confirm
            ////    Potential corner: if user selects more than initially indicated, prompt user if they want to select
            ////    more than initially indicated? Or just make array whatever size of input??
            ////    Remove any accepted requests
            ////    Potentially break this down for
            else {
                int select = 0;
                do {
                    String message = "Would you like to confirm a friend or group member?\n"
                            + "Options: "
                            + "\t1. Friend\n"
                            + "\t2. Groupmember\n"
                            + "\t3. Exit\n";
                    select = 0;
                    do {
                        select = UserInput.getInt(message);
                    } while (select != 1 || select != 2 || select != 3);

                    if (select == 1) {
                        message = "Please select userID to confirm friendship: ";
                        String userID2 = UserInput.getID(message);

                        //check if input is valid
                        //look through pending friends
                        String insertSQL = "INSERT INTO friends(userID1, userID2, JDate, message) VALUES(?, ?, ?, ?)";
                        prep = con.prepareStatement(insertSQL);
                        prep.setString(1, userID1);
                        prep.setString(2, userID2);
                        prep.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
                        prep.setString(4, pendingFriends.getString(2));
                        prep.executeUpdate();
                        prep.close();
                    } else {
                        message = "Please select groupID you want to confirm for: ";
                        String gID = UserInput.getID(message);

                        //confirm that this person is manager
                        //look through managed groups

                        message = "Please select userID of new group member: ";
                        String userID2 = UserInput.getID(message);

                        //confirm that this person has sent in a request
                        //look through group for that grouprequests
                        String insertSQl = "INSERT INTO groupMembership(gID, userID) VALUES(?, ?)";
                        prep = con.prepareStatement(insertSQl);
                        prep.setString(1, gID);
                        prep.setString(2, userID2);
                        prep.executeUpdate();
                        prep.close();
                    }
                }while(select != 3);
            }

            //// 6. Any requests that were not accepted are now deleted
            String delete = "DELETE FROM pendingFriends\n"
                    + "WHERE toID = ?";
            prep = con.prepareStatement(delete);
            prep.setString(1, userID1);
            prep.executeUpdate();

            groupsManaged.first();
            while(groupsManaged.next()){
                delete = "DELETE FROM pendingGroupmembers\n"
                        + "WHERE gID = ?";
                prep = con.prepareStatement(delete);
                prep.setString(1, groupsManaged.getString(1));
                prep.executeUpdate();
            }

            prep.close();
            con.close();
            groupsManaged.close();
            pendingFriends.close();

        } catch (SQLException Ex){
            System.out.println("Friends >> Error: "
                + Ex.toString());
        }

        return;
    }

    //make private method to get all friend request

    //make private method to get all group requests

    //make private method to insert friends

    //make pribate method to insert groups

    //make private method to delete pendingFriends

    //make private method to delete pendingGroupmembers

    /**
     * This task supports the browsing of the user’s friends and of their friends’ profiles. It first
     displays each of the user’s friends’ names and userIDs and those of any friend of those friends.
     Then it allows the user to either retrieve a friend’s entire profile by entering the appropriate
     userID or exit browsing and return to the main menu by entering 0 as a userID. When selected,
     a friend’s profile should be displayed in a nicely formatted way, after which the user should be
     prompted to either select to retrieve another friend’s profile or return to the main menu.
     *
     */
    public void displayFriends(String userID1){
        try{
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            Set<String> toDisplay = new HashSet<String>();

            ///// 1. List the userIDs of all friends of userID1 and the friends' friends (excluding userID1)
            ///// Make sure to remove duplicates so add all users to display into a set first
            String selectSQL = "SELECT userID\n"
                    + "FROM FRIENDS\n"
                    + "WHERE userID1 = ? OR userID2 = ?\n";
            PreparedStatement prep = con.prepareStatement(selectSQL);
            prep.setString(1, userID1);
            prep.setString(2, userID1);
            ResultSet userFriends = prep.executeQuery();

            while(userFriends.next()){
                toDisplay.add(userFriends.getString(1));
                selectSQL = "SELECT userID\n"
                        + "FROM FRIENDS\n"
                        + "WHERE userID1 = ? OR userID2 = ?\n";
                prep = con.prepareStatement(selectSQL);
                prep.setString(1, userID1);
                prep.setString(2, userID1);
                ResultSet friendFriends = prep.executeQuery();

                while(friendFriends.next()){
                    toDisplay.add(friendFriends.getString(1));
                }

                friendFriends.close();
            }

            userFriends.close();

            //create iterator to display all the choices

            //// 2. Ask userID1 to input an userID
            //// if 0, return back
            //// for anyother userID, retrieve profile information (excluding password)
            //// prompt user if they want to look at more profiles
            String message = "Would you like to look up a profile?\n"
                    + "Options: \n"
                    + "1: Yes\n"
                    + "2: No \n";
            int keepLooking = UserInput.getInt(message);

            while(keepLooking == 1) {
                String userID2 = UserInput.getID("Please enter the ID of the user you want to look at: ");
                if(toDisplay.contains(userID2)){
                    selectSQL = "SELECT userID, name, email, date_of_birth, lastLogin\n"
                            + "FROM PROFILE\n"
                            + "WHERE userID = ?";
                    prep = con.prepareStatement(selectSQL);
                    prep.setString(1, userID2);
                    ResultSet rs = prep.executeQuery();

                    rs.next();
                        System.out.print("UserID: " + userID2
                                        + "\nName: " + rs.getString(2)
                                        + "\nEmail: " + rs.getString(3)
                                        + "\nDate of Birth: " + rs.getDate(4)
                                        + "\nLast Login: " + rs.getDate(5));

                    message = "Would you like to look up another profile?\n"
                            + "Options: \n"
                            + "\t1: Yes\n"
                            + "\t2: No \n";
                    keepLooking = UserInput.getInt(message);

                    rs.close();
                } else{
                    System.out.println("This user if not accessible.");
                    message = "Would you like to try a different user?\n"
                            + "Options: \n"
                            + "1: Yes\n"
                            + "2: No \n";
                    keepLooking = UserInput.getInt(message);
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
