create or replace trigger friendReqMsg
	after insert on pendingFriends
	for each row
	DECLARE
		nmsgID varchar(20);
    begin
    IF :new.message IS NOT NULL
    THEN 
		SELECT to_char(count(msgID) + 1) INTO nmsgID
		FROM messages;
		
		insert into messages (msgID, fromID, message, toUserID, dateSent) 
		values(nmsgID,:new.fromID, :new.message, :new.toID, current_date);
		--insert into messageRecipient values(nmsgID, :new.toID); not sure if this is covered by the other trigger
    END IF;
	end;
/