/*
1. If new mesagges sent to a group:
    1.1 Get all userIDs in groupmembership where groupID = groupID to which the messages was sent
    1.2 Add all userIDs with :new.megID to messageRecipient (Duplicating the msg for each user is not necessary) 

SQL for testing: 
	--------------Create memberships---------------------
	insert into groupmembership values ('1', '1', 'user');
	insert into groupmembership values ('1', '2', 'user');
	insert into groupmembership values ('1', '3', 'user');
	
	--------------Insert and check if was created---------
	INSERT INTO messages (msgID,fromID,message,toGroupID,toUserID,dateSent) VALUES ('333',43,'This is message to group 1','1',NULL,'06-Aug-18');
	select * from messageRecipient where msgID='333';

*/
create or replace trigger sendMessagetoGroup
    after insert on messages 
    for each row
    begin 
        IF :new.toGroupID IS NOT NULL 
        THEN
            insert into messageRecipient (msgID, userID)
            SELECT :new.msgID, g.userID
            FROM groupMembership g
            WHERE g.gID = :new.toGroupID; -- you forgot : here
        end if;
    end;
/
