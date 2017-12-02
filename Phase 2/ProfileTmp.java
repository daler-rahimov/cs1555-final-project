
import java.sql.Date;
import java.sql.Timestamp;

/*
 * This is tmp class needs to be merged with Profile class 
* Since the profile class is not fully integrated added this for now 
 */

/**
 *
 * @author Daler
 */
public class ProfileTmp {
    private String userID;
    private String name;
    private String password;
    private Date dateOfBirth;
    private Timestamp lastlogin;
    private String email;

    public ProfileTmp(String userID, String name, String password, Date dateOfBirth, Timestamp lastlogin, String email) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.lastlogin = lastlogin;
        this.email = email;
    }

    public ProfileTmp(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public ProfileTmp() {
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

    public Date getDateOfBirth() {
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

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setLastlogin(Timestamp lastlogin) {
        this.lastlogin = lastlogin;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
