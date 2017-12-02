
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

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
            List<Profile> allFriendsprofiles = new LinkedList<Profile>();

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
                allFriendsprofiles.add(new Profile(rs.getString(1), rs.getString(2)));
            }

            //get all frieds if user in in userid1
            selectSQL = "SELECT FRIENDS.USERID2, PROFILE.NAME\n"
                    + "FROM FRIENDS\n"
                    + "INNER JOIN PROFILE ON FRIENDS.USERID2=PROFILE.USERID\n"
                    + "WHERE FRIENDS.USERID1=?";
//            stmt = con.createStatement();
            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, userID);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                allFriendsprofiles.add(new Profile(rs.getString(1), rs.getString(2)));
            }

            System.out.println("Lits of all of your friends:");
            for (int i = 0; i < allFriendsprofiles.size(); i++) {
                System.out.println(allFriendsprofiles.get(i).getUserID() + " " + allFriendsprofiles.get(i).getName());
            }
            ////////////////////////////////////////////////////////////////////

            /////////////////// 2. get user id /////////////////////////////////
            ////////////////////////////////////////////////////////////////////
            boolean isInFriendList = false;
            Profile toUser = null;
            do {
                String toUserID = UserInput.getID();
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
//            System.out.println("msgStr = " + msgStr);
            ////////////////////////////////////////////////////////////////////

            /////////////// 4. send message to the user db insert///////////////
            ////////////////////////////////////////////////////////////////////
            // get max values of msgID. Assuption id is alway numeric
            selectSQL = "select max(to_number(regexp_substr(msgid, '\\d+'))) msgid from MESSAGES";
            stmt = con.createStatement();
            rs = stmt.executeQuery(selectSQL);
            int nextMsgIDInt = 0;
            if (rs.next()) {
                nextMsgIDInt = rs.getInt(1) + 1;
            }
            java.util.Date currDate = new java.util.Date();
            Message message = new Message(Integer.toString(nextMsgIDInt), userID, msgStr, toUser.getUserID(), null, new Date(currDate.getTime()));
            if (message.insertToDb(con) == 0) {
                System.out.println("Message has been sent");
            } else {
                System.out.println("Failed to send a message DB errer");
            }
            ////////////////////////////////////////////////////////////////////
            stmt.close();
            rs.close();
        } catch (SQLException Ex) {
            System.out.println("Message >> Error: "
                    + Ex.toString());
        }
    }

    /**
     * 9. sendMessageToGroup With this the user can send a message to a
     * recipient group, if the user is within the group. Every member of this
     * group should receive the message. The user should be prompted to enter
     * the text of the message, which could be multi-lined. Then the created new
     * message should be \sent" to the user by adding appropriate entries into
     * the messages and messageRecipients relations by creating a trigger. The
     * user should lastly be shown success or failure feedback. Note that if the
     * user sends a message to one friend, you only need to put the friend's
     * userID to ToUserID in the table of messages. If the user wants to send a
     * message to a group, you need to put the group ID to ToGroupID in the
     * table of messages and use a trigger to populate the messageRecipient
     * table with proper user ID information as dened by the groupMembership
     * relation.
     *
     * @param userID the id of the user that is currently logged in
     */
    public static void sendMessageToGroup(String userID) {
        //1. Show all groups of current user
        //2. Get groupID
        //3. Get message multilined. Show the name of the group
        //4. Send the message to the group

        try {
            /////////// 1. show all friends of the user ////////////////////////
            ////////////////////////////////////////////////////////////////////
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            List<Group> allGroups = new LinkedList<Group>();

            // get all frieds if user in in userid2
            String selectSQL = "SELECT GROUPS.GID, GROUPS.NAME\n"
                    + "FROM GROUPS\n"
                    + "INNER JOIN GROUPMEMBERSHIP ON GROUPS.GID=GROUPMEMBERSHIP.GID\n"
                    + "WHERE GROUPMEMBERSHIP.USERID = ?";
            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, userID);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                allGroups.add(new Group(rs.getString(1), rs.getString(2), null));
            }
            System.out.println("Lits of all grups you are memeber of:");
            for (int i = 0; i < allGroups.size(); i++) {
                System.out.println(allGroups.get(i).getgID() + " " + allGroups.get(i).getName());
            }
            ////////////////////////////////////////////////////////////////////

            /////////////////// 2. get group id /////////////////////////////////
            ////////////////////////////////////////////////////////////////////
            boolean isInGroupList = false;
            Group toGroup = null;
            do {
                String toGroupID = UserInput.getID();
                //validate
                for (int i = 0; i < allGroups.size(); i++) {
                    if (toGroupID.equals(allGroups.get(i).getgID())) {
                        isInGroupList = true;
                        toGroup = allGroups.get(i);
                    }
                }
                if (!isInGroupList) {
                    System.out.println("This group is not in your group list!");
                }
            } while (!isInGroupList);
            ////////////////////////////////////////////////////////////////////

            //////// 3.  Get message multilined. Show the name of the group///
            ////////////////////////////////////////////////////////////////////
            System.out.println("For group =>" + toGroup.getName());
            String msgStr = UserInput.getMessage();
//            System.out.println("msgStr = " + msgStr);
            ////////////////////////////////////////////////////////////////////

            /////////////// 4. Send the message to the group///////////////
            ////////////////////////////////////////////////////////////////////
            // get max values of msgID. Assuption id is alway numeric
            selectSQL = "select max(to_number(regexp_substr(msgid, '\\d+'))) msgid from MESSAGES";
            stmt = con.createStatement();
            rs = stmt.executeQuery(selectSQL);
            int nextMsgIDInt = 0;
            if (rs.next()) {
                nextMsgIDInt = rs.getInt(1) + 1;
            }
            java.util.Date currDate = new java.util.Date();
            Message message = new Message(Integer.toString(nextMsgIDInt), userID, msgStr, null, toGroup.getgID(), new Date(currDate.getTime()));
            if (message.insertToDb(con) == 0) {
                System.out.println("Message has been sent");
            } else {
                System.out.println("Failed to send a message DB errer");
            }
            ////////////////////////////////////////////////////////////////////
            stmt.close();
            rs.close();
        } catch (SQLException Ex) {
            System.out.println("Message>sendMessageToGroup >> Error: "
                    + Ex.toString());
        }
    }

    /**
     * 10. displayMessages When the user selects this option, the entire
     * contents of every message sent to the user should be displayed in a
     * nicely formatted way.
     *
     * @param userID current user that is logged in
     */
    public static void displayMessages(String userID) {
        //1. Get all messages for a given user
        //2. Display all those messages
        try {
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            List<Message> allMessages = new LinkedList<Message>();

            String selectSQL = "SELECT M.MSGID, M.FROMID, M.MESSAGE, M.TOGROUPID, M.TOUSERID,M.DATESENT\n"
                    + "FROM MESSAGES M \n"
                    + "INNER JOIN MESSAGERECIPIENT MR ON MR.MSGID=M.MSGID\n"
                    + "WHERE MR.USERID=?";
            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, userID);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                //public Message(String msgID, String fromID, String message, String toUserID, String toGroupID, Date dateSent) {
                allMessages.add(new Message(rs.getString("msgID"), rs.getString("fromID"), rs.getString("message"), rs.getString("toUserID"), rs.getString("toGroupID"), rs.getDate("dateSent")));
            }
            System.out.println("Lits of all messages sent to you:");
            System.out.println("--------------------------------");
            System.out.format("%-8s%-8s%-14s%-200s\n", "msgID", "fromID", "DateSent", "message");
            for (int i = 0; i < allMessages.size(); i++) {
                System.out.format("%-8s%-8s%-14s%-200s\n", allMessages.get(i).getMsgID(), allMessages.get(i).getFromID(), allMessages.get(i).getDateSent(), allMessages.get(i).getMessage());
            }
            System.out.println("--------------------------------");
        } catch (SQLException Ex) {
            System.out.println("Message>displayMessages()  >> Error: "
                    + Ex.toString());
        }
    }

    /**
     * 11. displayNewMessages This should display messages in the same fashion
     * as the previous task except that only those messages sent since the last
     * time the user logged into the system should be displayed.
     *
     * @param userID current user logged in
     */
    public static void displayNewMessages(String userID) {

        // 2. Get all the messages from lastLoggedIn onword
        // 3. Display it
        try {
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            List<Message> allMessages = new LinkedList<Message>();
            Timestamp lastLogin = null;

            String selectSQL = "SELECT M.MSGID, M.FROMID, M.MESSAGE, M.TOGROUPID, M.TOUSERID,M.DATESENT, P.LASTLOGIN\n"
                    + "FROM MESSAGES M \n"
                    + "INNER JOIN MESSAGERECIPIENT MR ON MR.MSGID=M.MSGID\n"
                    + "INNER JOIN PROFILE P ON MR.USERID = P.USERID\n"
                    + "WHERE MR.USERID=? AND \n"
                    + "        TO_DATE (TO_CHAR (P.LASTLOGIN, 'YYYY-MON-DD HH24:MI:SS'),'YYYY-MON-DD HH24:MI:SS')<= M.DATESENT";// 1. DATESENT is after last logged in

            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, userID);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                //public Message(String msgID, String fromID, String message, String toUserID, String toGroupID, Date dateSent) {
                allMessages.add(new Message(rs.getString("msgID"), rs.getString("fromID"), rs.getString("message"), rs.getString("toUserID"), rs.getString("toGroupID"), rs.getDate("dateSent")));
                lastLogin = rs.getTimestamp("lastlogin");
            }
            System.out.print("Lits of all messages since you last logged in: ");
            if (lastLogin instanceof Timestamp) {
                System.out.print(lastLogin + "\n");
            }
            System.out.println("-----------------------------------------------------------------");
            System.out.format("%-8s%-8s%-14s%-200s\n", "msgID", "fromID", "DateSent", "message");
            System.out.println("-----------------------------------------------------------------");

            for (int i = 0; i < allMessages.size(); i++) {
                System.out.format("%-8s%-8s%-14s%-200s\n", allMessages.get(i).getMsgID(), allMessages.get(i).getFromID(), allMessages.get(i).getDateSent(), allMessages.get(i).getMessage());
            }
            System.out.println("-----------------------------------------------------------------");
        } catch (SQLException Ex) {
            System.out.println("Message>displayMessages()  >> Error: "
                    + Ex.toString());
        }
    }

    /**
     * 14. topMessages Display top K who have sent to received the highest
     * number of messages during for the past x months. x and K are input
     * parameters to this function.
     */
    public static void topMessages() {

        // 3. count number of sent + received messages for each user for past X months
        // 4. get top K of those users and display them
        // 1. get top K
        int topK = UserInput.getInt("Enter Top K values >");
        // 2. get past X months
        int pastXMonths = UserInput.getInt("Enter past X months >");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -(pastXMonths));
        java.util.Date curreSubDate = cal.getTime();
        java.sql.Date afterThiDate = new java.sql.Date(curreSubDate.getTime());
        System.out.println(curreSubDate);
        System.out.println(afterThiDate);

        try {
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            // for past Date  Top K
            String selectSQL = "select * from \n"
                    + "(\n"
                    + "  select sCount.userID, sCount.name , sCount.countSent+rCount.countReceived as RScount\n"
                    + "  from\n"
                    + "  (\n"
                    + "    select p.userID, count(mr.MSGID) as countReceived \n"
                    + "    from MESSAGERECIPIENT mr \n"
                    + "    inner join profile p on mr.USERID = p.USERID\n"
                    + "    inner join messages m on mr.MSGID = m.MSGID\n"
                    + "    where m.DATESENT>?\n"
                    + "    group by p.USERID\n"
                    + "  ) rCount\n"
                    + "  INNER JOIN \n"
                    + "  (\n"
                    + "    select p.userID, p.name, count(m.MSGID) as countSent \n"
                    + "    from messages m \n"
                    + "    inner join profile p on m.fromID = p.USERID\n"
                    + "    where m.dateSent>?\n"
                    + "    group by p.USERID , p.name\n"
                    + "  ) sCount\n"
                    + "  ON rCount.userID = sCount.userID\n"
                    + "  ORDER BY sCount.countSent+rCount.countReceived DESC\n"
                    + "  ) t1\n"
                    + "WHERE ROWNUM <= ?\n"
                    + "ORDER BY ROWNUM";// 1. DATESENT is after last logged in

            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setDate(1, afterThiDate);
            preparedStatement.setDate(2, afterThiDate);
//            preparedStatement.setDate(2, new Date(118, 1, 17));
            preparedStatement.setInt(3, topK);
            ResultSet rs = preparedStatement.executeQuery();

            System.out.println("-------------------------------------------");
            System.out.format("%-8s%-20s%-10s\n", "userID", "name", "RSCount");
            System.out.println("-------------------------------------------");
            while (rs.next()) {
                System.out.format("%-8s%-20s%-10s\n", rs.getString("userID"), rs.getString("name"), rs.getInt("RScount"));
            }

            System.out.println("-------------------------------------------");
        } catch (SQLException Ex) {
            System.out.println("Message>topMessages()  >> Error: "
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
            System.err.println("Message>insertToDb() >> Error: "
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
