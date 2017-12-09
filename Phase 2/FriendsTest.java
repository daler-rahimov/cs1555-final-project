public class FriendsTest {
    public static void main(String args[]){
        Friends.confirmFriendship("6");
        Friends.confirmFriendship("66");
        System.out.println(Friends.initiateFriendship("25"));
        Friends.displayFriends("7"); //Has friends, can test both correct user and incorrect user
    }
}
