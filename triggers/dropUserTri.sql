-----------trigger dropUser---------------
/*
Consideration: 
	1. If a user delted for the db (profile):
		1.1 Table friends -> all the enteries will be delete hence unfiended 
		1.2 Table pendingGroupmembers -> all the pending will be delted 
		1.3 Table pendingFriends -> all pending requestis will be deleted 
		1.4 Table groupMembership -> all memberships will be deleted 
		1.5 Table messages :
			5.1 If user sent to a group it will not be deleted (preformance over storage)
			5.2 If user sent to a user 
				5.2.1 Recipient user still exits (toUserID not null) will not be deleted 
				5.2.2 Recipient user does not eixit will be deleted
			5.3 If received by the this user
				5.3.1 Sender user still exits will not be deleted 
				5.3.2 Sender user does not exit will be deleted 
		1.6 Table messageRecipient -> all deleted 
*/


create or replace trigger dropUser
        before delete on profile
        for each row
        begin
                /*1.1*/delete from friends where userID1 = :new.userID or userID2 = :new.userID;
				/*1.2*/delete from pendingGroupmembers where userID = :new.userID;
				/*1.3*/delete from pendingFriends where fromID = :new.userID or toID = :new.userID;
				/*1.4*/delete from groupMembership where userID = :new.userID;
				/*1.5*/delete from messages where (fromID = :new.userID and toUserID = null) or (toUserID = :new.userID and fromID = null);
				/*1.6*/delete from messageRecipient where userID = :new.userID;
        end;
/


