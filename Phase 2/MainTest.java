/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daler
 */
public class MainTest {

    public static void main(String avg[]) {
        System.out.println("Calling sentMessageToUser for testing . ");
        Message.isTest = true;
        Message.sentMessageToUser("1");
        System.out.println("******************************************");

        System.out.println("Calling sendMessageToGroup for testing . ");
        Message.sendMessageToGroup("83");
        System.out.println("******************************************");

        System.out.println("Calling displayMessages for testing . ");
        Message.displayMessages("83");
        System.out.println("******************************************");

        System.out.println("Calling displayNewMessages for testing . ");
        Message.displayNewMessages("1");
        System.out.println("******************************************");

        System.out.println("Calling topMessages with k=10 x=10 for testing . ");
        Message.topMessages();
        System.out.println("******************************************");

        Group.isTest = true;
        System.out.println("Calling createGroup for testing . ");
        Group.createGroup("2");
        System.out.println("******************************************");

        System.out.println("Calling initiateAddingGroup for testing . ");
        Group.initiateAddingGroup("1");
        System.out.println("******************************************");

        System.out.println("Testing of Profile");
        Profile.isTest = true;
        //System.out.println(Profile.login()); //returns false, "1", "TMA58U6JG"
        System.out.println("LogIn of user 1");
        System.out.println("\tWas the profile logged in?: " + Profile.login()); //returns true, "1", "TMA58URO6JG"
        System.out.println("**************************");

        System.out.println("Logging out user 2");
        Profile.logout("2");
        System.out.println("**************************");

        System.out.println("User 10 is being dropped");
        Profile.dropUser("10");
        System.out.println("**************************");

        System.out.println("A new user was created");
        Profile.createUser();
        System.out.println("**************************");

        System.out.println("Testing threeDegrees between user 43 and 48");
        //Profile.threeDegrees(); //direct friends, enter 43 and 44
        //Profile.threeDegrees(); //2 hops, enter 43 and 47
        //Profile.threeDegrees(); //3 hops, enter 43 and 48
        Profile.threeDegrees(); //no path, enter 43 and 45
        System.out.println("**************************");

        System.out.println("We will search for users that match \"12\", \"Bob\", \"Gabby\"");
        Profile.searchForUser();
        System.out.println("**************************");

        //hi
        System.out.println("We now are testing Friends");
        Friends.isTest = true;
        Friends.isTest2 = true;

        System.out.println("Confirm all requests for user 6");
        Friends.confirmFriendship("6");
        System.out.println("**************************");

        System.out.println("Manually confirm a group member that user 66 manages");
        Friends.confirmFriendship("66");
        System.out.println("**************************");

        System.out.println("User 25 will send a friend request to user 15");
        Friends.initiateFriendship("25");
        System.out.println("**************************");

        System.out.println("We will look at user 7's friends and look a profile that is selectable");
        Friends.displayFriends("7"); //Has friends, can test both correct user and incorrect user
        System.out.println("**************************");

    }
}
