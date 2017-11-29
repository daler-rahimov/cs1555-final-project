
import java.sql.Date;

/*
 * This is only for testing of Profile class
 */

public class ProfileTest {
    public static void main(String args[]){
        Profile.createUser("Maya", "mel118", new Date(new java.util.Date().getTime()), "password");
    }
}
