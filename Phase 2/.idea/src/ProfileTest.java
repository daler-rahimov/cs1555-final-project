public class ProfileTest {
    public static void main(String args[]){
        //System.out.print(Profile.login("1", "TMA58U6JG")); //returns false
        //System.out.print(Profile.login("1", "TMA58URO6JG")); //returns true
        //Profile.logout("2");
        //Profile.dropUser("6");
        //Profile.createUser();
        Profile.threeDegrees("43", "44"); //direct friends
        Profile.threeDegrees("43", "47"); //2 hops
        Profile.threeDegrees("43", "48"); //3 hops
        Profile.threeDegrees("43", "45"); //no path
        //Profile.searchForUser();

    }
}
