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
    public void initiateFriendship(String userID1, String userID2, String message){
        ///// 1. Connect to database


        ///// 2. Check to make sure not friends


        ///// 3. If not already friends, insert into pendingFriendships

    };

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
    public void confirmFriendship(int userID1, int userID2){

    };

    /**
     * This task supports the browsing of the user’s friends and of their friends’ profiles. It first
     displays each of the user’s friends’ names and userIDs and those of any friend of those friends.
     Then it allows the user to either retrieve a friend’s entire profile by entering the appropriate
     userID or exit browsing and return to the main menu by entering 0 as a userID. When selected,
     a friend’s profile should be displayed in a nicely formatted way, after which the user should be
     prompted to either select to retrieve another friend’s profile or return to the main menu.
     *
     */
    public void displayFriends(int userID1){


    };
}
