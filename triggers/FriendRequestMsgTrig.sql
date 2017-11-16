create or replace trigger friendReqMsg
	after insert on pendingFriends
	for each row
	DECLARE
		msgID varchar(20);
    begin
    IF :new.message IS NOT NULL
    THEN 
		SELECT to_char(count(msgID)) INTO msgID
		FROM messages
		GROUP BY message;
		
		insert into message values(msgID,:new.fromID, :new.message, null, :new.toID, current_date);
		insert into messageRecipient values(:new.msgID, :new.toID);
    END IF;
	end;
/