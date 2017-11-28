
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/*
-msgID: int
-fromID: int
-message: String
-toUserID: int
-toGroupID: int
-dateSent: datex    

Methods 
-getters and setters
-sentMessageToUser
-sendMessageToGroup
-displayMessages
-displayNewMessages
-topMessages
 */
public class Message {

    private String msgID;
    private String fromID;
    private String message;
    private String toUserID;
    private String toGroupID;
    private Date dateSent;

    /**
     * 8. sendMessageToUser With this the user can send a message to one friend
     * given his userID. The application should display the name of the
     * recipient and the user should be prompted to enter the text of the
     * message, which could be multi-lined. Once entered, the message should be
     * \sent" to the user by adding appropriate entries into the messages and
     * messageRecipients relations by creating a trigger. The user should lastly
     * be shown success or failure feedback.
     *
     * @param userID the id of the user that is currently logged in
     */
    public static void sentMessageToUser(String userID) {
        try {
            /////////// 1. show all friends of the user ////////////////////////
            ////////////////////////////////////////////////////////////////////
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            List<ProfileTmp> allFriendsprofiles = new LinkedList<ProfileTmp>();

            // get all frieds if user in in userid2
            String selectSQL = "SELECT FRIENDS.USERID1, PROFILE.NAME\n"
                    + "FROM FRIENDS\n"
                    + "INNER JOIN PROFILE ON FRIENDS.USERID1=PROFILE.USERID\n"
                    + "WHERE FRIENDS.USERID2=?";
            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, userID);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                allFriendsprofiles.add(new ProfileTmp(rs.getString(1), rs.getString(2)));
            }

            //get all frieds if user in in userid1
            selectSQL = "SELECT FRIENDS.USERID2, PROFILE.NAME\n"
                    + "FROM FRIENDS\n"
                    + "INNER JOIN PROFILE ON FRIENDS.USERID2=PROFILE.USERID\n"
                    + "WHERE FRIENDS.USERID1=?";
            stmt = con.createStatement();
            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, userID);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                allFriendsprofiles.add(new ProfileTmp(rs.getString(1), rs.getString(2)));
            }

            System.out.println("Lits of all of your friends:");
            for (int i = 0; i < allFriendsprofiles.size(); i++) {
                System.out.println(allFriendsprofiles.get(i).getUserID() + " " + allFriendsprofiles.get(i).getName());
            }
            ////////////////////////////////////////////////////////////////////
            
            /////////////////// 2. get user id /////////////////////////////////
            ////////////////////////////////////////////////////////////////////
            boolean isInFriendList = false;
            ProfileTmp toUser = null;
            do {
                String toUserID = UserInput.getUserID();
                //validate 
                for (int i = 0; i < allFriendsprofiles.size(); i++) {
                    if (toUserID.equals(allFriendsprofiles.get(i).getUserID())) {
                        isInFriendList = true;
                        toUser = allFriendsprofiles.get(i);
                    }
                }
                if (!isInFriendList) {
                    System.out.println("This user is not in your frieds list!");
                }
            } while (!isInFriendList);
            ////////////////////////////////////////////////////////////////////
            
            ///////////// 3. show the name and and getMessageText///////////////
            ////////////////////////////////////////////////////////////////////
            System.out.println("For user =>" + toUser.getName());
            String msgStr = UserInput.getMessage();
            System.out.println("msgStr = " + msgStr);
            ////////////////////////////////////////////////////////////////////
            
            /////////////// 4. send message to the user db insert///////////////
            ////////////////////////////////////////////////////////////////////
            // get max values of msgID. Assuption is alway nuber 
            selectSQL = "select max(to_number(regexp_substr(msgid, '\\d+'))) msgid from MESSAGES";
            stmt = con.createStatement();
            rs = stmt.executeQuery(selectSQL);
            int nextMsgIDInt = 0;
            if (rs.next()) {
                nextMsgIDInt = rs.getInt(1) + 1;
            }
            java.util.Date currDate = new java.util.Date();
            Message message = new Message(Integer.toString(nextMsgIDInt), userID, msgStr, toUser.getUserID(), null, new Date(currDate.getTime()));
            message.insertToDb(con);
            ////////////////////////////////////////////////////////////////////
            con.close();
            stmt.close();
            rs.close();
        } catch (SQLException Ex) {
            System.out.println("Message >> Error: "
                    + Ex.toString());
        }
    }

    /**
     *
     * @param con Connection to the database
     * @return 0 if inserted >0 otherwise
     */
    public int insertToDb(Connection con) {
//        INSERT INTO messages (msgID,fromID,message,toGroupID,toUserID,dateSent) VALUES (302,73,'urna. Nunc',8,Null,'25-Sep-18');
        String insertSQL = "INSERT INTO messages ("
                + "msgID,"
                + "fromID"
                + ",message"
                + ",toGroupID"
                + ",toUserID"
                + ",dateSent"
                + ") "
                + "VALUES ("
                + " ?"
                + ",?"
                + ",?"
                + ",?"
                + ",?"
                + ",?"
                + ")";
        try {
            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(insertSQL);

            preparedStatement.setString(1, msgID);
            preparedStatement.setString(2, fromID);
            preparedStatement.setString(3, message);
            preparedStatement.setString(4, toGroupID);
            preparedStatement.setString(5, toUserID);
            preparedStatement.setDate(6, dateSent);

            ResultSet rs = preparedStatement.executeQuery();
        } catch (SQLException Ex) {
            System.err.println("Message >> Error: "
                    + Ex.toString());
            return 1;
        }
        return 0;
    }

    public Message(String msgID, String fromID, String message, String toUserID, String toGroupID, Date dateSent) {
        this.msgID = msgID;
        this.fromID = fromID;
        this.message = message;
        this.toUserID = toUserID;
        this.toGroupID = toGroupID;
        this.dateSent = dateSent;
    }

    public String getMsgID() {
        return msgID;
    }

    public String getFromID() {
        return fromID;
    }

    public String getMessage() {
        return message;
    }

    public String getToUserID() {
        return toUserID;
    }

    public String getToGroupID() {
        return toGroupID;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    public void setToGroupID(String toGroupID) {
        this.toGroupID = toGroupID;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

}
