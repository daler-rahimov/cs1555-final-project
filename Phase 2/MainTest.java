/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
import java.util.*;

/**
 *
 * @author Daler and Maya
 */
public class MainTest {

    public static void main(String avg[]) {
        try {

            System.out.println("Calling sentMessageToUser for testing: User 1 will send message to 100");
            Message.isTest = true;
            Message.sentMessageToUser("1");
            System.out.println("******************************************");

            System.out.println("Calling displayNewMessages for testing: User 100 will display all new messages, including one just sent by user 1");
            Message.displayNewMessages("100");
            System.out.println("******************************************");

            System.out.println("Calling sendMessageToGroup for testing: User 83 will send message to 1");
            Message.sendMessageToGroup("83");
            System.out.println("******************************************");

            System.out.println("Calling displayMessages for testing: User 67 will display all their messages, including the group message from user 83");
            Message.displayMessages("67");
            System.out.println("******************************************");

            System.out.println("Calling topMessages with k=10 x=10 for testing:");
            Message.topMessages();
            System.out.println("******************************************");

            Group.isTest = true;
            System.out.println("Calling createGroup for testing: User 2 will create a new group");
            Group.createGroup("2");
            System.out.println("******************************************");

            System.out.println("Calling initiateAddingGroup for testing: User 1 to initaite to group 1");
            Group.initiateAddingGroup("3");
            System.out.println("******************************************");

            //add one where the group limit is full
            Group.isTest = false;
            Group.isNTest = true;
            System.out.println("Calling initiateAddingGroup for testing: User 3 to initaite to group 2, which limit has been reached");
            Group.initiateAddingGroup("1");
            System.out.println("******************************************");

            System.out.println("Testing of Profile");
            Profile.isTest2 = true;
            System.out.println("Unsuccessful login of user 1 due to typo");
            System.out.println("\tWas the profile logged in?: " + Profile.login()); //returns false, "1", "TMA58U6JG"
            System.out.println("**************************");

            Profile.isTest2 = false;
            Profile.isTest = true;

            System.out.println("Successful LogIn of user 1");
            System.out.println("\tWas the profile logged in?: " + Profile.login()); //returns true, "1", "TMA58URO6JG"
            System.out.println("**************************");

            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            System.out.println("Logging out user 2");
            Profile.logout("2");
            String selectSQL = "SELECT userID, lastLogin\n"
                    + "FROM PROFILE\n"
                    + "WHERE userID = ?";
            PreparedStatement prep = con.prepareStatement(selectSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            prep.setString(1, "2");
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {
                System.out.println("\tLast login of user " + rs.getString(1) + " at " + rs.getTimestamp(2));
            }
            System.out.println("**************************");

            System.out.println("User 10 is being dropped for the first time");
            Profile.dropUser("10");
            System.out.println("**************************");

            System.out.println("User 10 is being dropped again for the second time.");
            Profile.dropUser("10");
            System.out.println("**************************");

            System.out.println("A new user was created Gabby was created");
            Profile.createUser();
            //show that the user was created
            selectSQL = "SELECT userID, name\n"
                    + "FROM PROFILE";
            Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stm.executeQuery(selectSQL);
            while (rs.next()) {
                if (rs.isLast()) {
                    System.out.println("\t New user " + rs.getString(1) + " is " + rs.getString(2));
                }
            }
            System.out.println("**************************");
            System.out.println("Testing threeDegrees between user 43 and 48, full 3 hops");
            System.out.println("**************************");

            System.out.println("We will search for users that match \"12\", \"Bob\", \"Gabby\"");
            Profile.searchForUser();
            System.out.println("**************************");

            System.out.println("We now are testing Friends");
            Friends.isTest = true;


            System.out.println("Confirm all requests for user 6");
            Friends.confirmFriendship("6");
            //show that there are no more pendingfriends for this person
            selectSQL = "SELECT fromID, toID\n"
                    + "FROM pendingFriends\n"
                    + "WHERE toID = ?";
            prep = con.prepareStatement(selectSQL);
            prep.setString(1, "6");
            rs = prep.executeQuery();
            int count = 0;
            while(rs.next()){
                count++;
            }
            if (count ==0) {
                System.out.println("All requests were deleted");
            }
            System.out.println("**************************");

            System.out.println("We will look at user 6's friends to show they have friended everyone in the previous command as well as look at a profile");
            Friends.displayFriends("6"); //Has friends, can test both correct user and incorrect user
            System.out.println("**************************");

            Friends.isTest2 = true;
            System.out.println("Manually confirm a group member that user 66 manages");
            Friends.confirmFriendship("66");
            selectSQL = "SELECT fromID, toID\n"
                    + "FROM pendingFriends\n"
                    + "WHERE toID = ?";
            prep = con.prepareStatement(selectSQL);
            prep.setString(1, "66");
            rs = prep.executeQuery();
            count = 0;
            while(rs.next()){
                count++;
            }
            if (count == 0) {
                System.out.println("All requests were deleted");
            }
            System.out.println("**************************");

            System.out.println("User 25 will send a friend request to user 15");
            Friends.initiateFriendship("25");
            //show that friendship was initiated
            System.out.println("**************************");

            System.out.println("User 25 will send a friend request to user 15: Will not be successful due to pending request already");
            Friends.initiateFriendship("25");
            //show that friendship was initiated
            System.out.println("**************************");
        } catch (SQLException Ex) {
             System.out.println("Maintest >> Error: "
                + Ex.toString());
        }
    }
}
