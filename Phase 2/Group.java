
import java.sql.*;
import java.util.*;

public class Group {

    public static boolean isTest = false;
    public static boolean isNTest = false;

    /**
     * 6. createGroup Given a name, description, and membership limit, add a new
     * group to the system, add the user as its first member with the role
     * manager.
     *
     * @param userID current user that is logged in
     */
    public static void createGroup(String userID) {
        // 1. get group name
        // 2. get description
        // 3. get limit
        // 4. add new group
        // 5. make current user a member with manager priviligoues

        String groupName;
        if (!isTest) {
            groupName = UserInput.getLine20("Enter a group name: ");
        } else {
            groupName = "AutoTest";
        }

        String discription;
        if (!isTest) {
            discription = UserInput.getLine200("Enter group description: ");
        } else {
            discription = "This is from auto Test";
        }

        int gLimit;
        if (!isTest) {
            gLimit = UserInput.getInt("Enter group limit > ");
        } else {
            gLimit = 100;
        }

        try {
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();

            // 4. add new group
            // 4.1 get next posible gID
            String selectSQL = "select max(to_number(regexp_substr(gID, '\\d+'))) msgid from GROUPS";

            Statement stmt = con.createStatement();
            String lock = "lock table " + "FRIENDS" + " in exclusive mode";
            stmt.execute(lock);

            ResultSet rs = stmt.executeQuery(selectSQL);
            int nextGID = 0;
            if (rs.next()) {
                nextGID = rs.getInt(1) + 1;
            }

            Group group = new Group(gLimit, Integer.toString(nextGID), groupName, discription);
            if (group.insertToDb(con) == 0) {
                System.out.println("Group has been added");
            } else {
                System.out.println("Failed to add group to DB errer");
            }
            con = sCon.getConnection();
            // 5. make current user a member with manager priviligoues
//            INSERT INTO groupMembership (gID, userID, role) VALUES (1, 29, 'manager');
            selectSQL = "INSERT INTO groupMembership ("
                    + "gID"
                    + ", userID"
                    + ", role"
                    + ") VALUES ("
                    + "  ?"
                    + ", ?"
                    + ", 'manager' "
                    + ")";
            PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, group.getgID());
            preparedStatement.setString(2, userID);
            preparedStatement.executeQuery();
            ////////////////////////////////////////////////////////////////////
            con.commit();
            stmt.close();
            rs.close();
        } catch (SQLException Ex) {
            System.out.println("Message >> Error: "
                    + Ex.toString());
        }
    }

    public Group(int gLimit, String gID, String name, String description) {
        this.gLimit = gLimit;
        this.gID = gID;
        this.name = name;
        this.description = description;
    }

    private int gLimit;
    private String gID;
    private String name;
    private String description;

    public int insertToDb(Connection con) {
//INSERT INTO groups (gID,name,description) VALUES (1,'Fusce Institute','feugiat tellus lorem eu metus. In lorem. Donec elementum, lorem ut aliquam iaculis,');
        String insertSQL = "INSERT INTO groups ("
                + "gID"
                + ",name"
                + ",description"
                + ", gLimit"
                + ") VALUES ("
                + " ?"
                + ",?"
                + ",?"
                + ",?"
                + ")";
        try {
            Statement stmt = con.createStatement();
            PreparedStatement preparedStatement = con.prepareStatement(insertSQL);

            preparedStatement.setString(1, gID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setInt(4, gLimit);

            preparedStatement.executeQuery();
            con.commit();
            stmt.close();

        } catch (SQLException Ex) {
            System.err.println("Group>insertToDb() >> Error: "
                    + Ex.toString());
            return 1;
        }
        return 0;
    }

    /**
     * 7. initiateAddingGroup Given a user and a group, create a pending request
     * of adding to group (if not violate the group's membership limit). The
     * user should be prompted to enter a message to be sent along with the
     * request and inserted in the pendingGroupmembers relation.
     *
     * @param userID current user that is logged in
     */
    public static void initiateAddingGroup(String userID) {
        // 1. Dispay all the groups that this user is not member of
        // 2. Get gID to which user want to sign up
        // 3. Check if gtoupmember + group Requests not > limit
        // 4. if not sent the request

        try {
            SocialPantherCon sCon = new SocialPantherCon();
            Connection con = sCon.getConnection();
            String selectSQL = "select distinct g.gID , g.name\n"
                    + "from groups g \n"
                    + "inner join groupmembership gm on gm.gID=g.gID\n"
                    + "where gm.userID <> ?";
            PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, userID);
            ResultSet rs = preparedStatement.executeQuery();

            System.out.println("List of all groups you are not member of: ");
            System.out.println("-------------------------------------------");
            System.out.format("%-5s%-20S\n", "gID", "gName");
            while (rs.next()) {
                System.out.format("%-5s%-20S\n", rs.getString("gID"), rs.getString("name"));
            }
            System.out.println("-------------------------------------------");

            // 2. Get gID
            int gID;
            if (isTest || isNTest) {
                gID = 1;
            } else if (isNTest) {
                gID = 2;
            } else{
                gID = UserInput.getInt("Enter a group you want to sign up for >");
            }
            String message;
            if (isTest || isNTest) {
                message = "This is from Auto Test MainTest";
            } else {
                message = UserInput.getLine200("Enter you message for the group (\\n for new line) ");
            }
            // 3. Check if gtoupmember + group Requests not > limit
            selectSQL = "select count(*) as count\n"
                    + "from groupmembership gm\n"
                    + "FULL OUTER JOIN pendinggroupmembers pm on gm.GID = pm.GID\n"
                    + "where gm.gID = ? or pm.gID = ?";
            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, Integer.toString(gID));
            preparedStatement.setString(2, Integer.toString(gID));
            rs = preparedStatement.executeQuery();
            int currMemberCount = Integer.MAX_VALUE;
            if (rs.next()) {
                currMemberCount = rs.getInt("count");
            } else {
                System.err.println("Group>initiateAddingGroup()  gId does not exist !!!");
                return;
            }

            selectSQL = "select gLimit from groups where gID = ?";
            preparedStatement = con.prepareStatement(selectSQL);
            preparedStatement.setString(1, Integer.toString(gID));
            rs = preparedStatement.executeQuery();
            int groupLimit = 0;
            if (rs.next()) {
                groupLimit = rs.getInt("gLimit");
            } else {
                System.err.println("Group>initiateAddingGroup()  gLimit does not exist !!!");
                return;
            }

            boolean isAllowed = false;
            if (groupLimit > currMemberCount) {
                isAllowed = true;
            } else {
                System.out.println("This group limit reached you cannot initiate request. ");
                return;
            }

            // send request
            if (isAllowed) {
                selectSQL = "INSERT INTO pendingGroupmembers ( "
                        + " gID "
                        + ", userID"
                        + ", message"
                        + ") VALUES ( "
                        + " ?"
                        + ",?"
                        + ",? )";
                preparedStatement = con.prepareStatement(selectSQL);
                preparedStatement.setString(1, Integer.toString(gID));
                preparedStatement.setString(2, userID);
                preparedStatement.setString(3, message);
                rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    System.out.println("Your request has been sent. ");
                }
            }

            ////////////////////////////////////////////////////////////////////
//            con.close();
            con.commit();
            rs.close();
        } catch (SQLIntegrityConstraintViolationException Ex) {
            System.out.println("You have already tried to join this group, confirmation is still pending");
        } catch (SQLException Ex) {
            System.out.println("Group>initiateAddingGroup() >> Error: "
                    + Ex.toString());
        }
    }

    public void setgID(String gID) {
        this.gID = gID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getgID() {
        return gID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Group(String gID, String name, String description) {
        this.gID = gID;
        this.name = name;
        this.description = description;
    }

}
