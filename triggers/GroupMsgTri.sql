--created new new message should be 'sent'
--to the user by updating appropriate entries
--into the messages and message recipients relations
create or replace trigger sendMessagetoGroup
    after insert on message
    for each row
    begin
       
    IF :new.GroupID != NULL
	TABLE
		SELECT userID
		FROM groupMembership as g
		WHERE g.groupID = new.GroupID;
		
		insert into messageRecipient values(:new.msgID, :new.toUserID);
	end if;
end;
/

