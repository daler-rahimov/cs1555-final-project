create or replace trigger sendMessageToUser
	after insert on messages
	for each row 
    begin
    IF :new.toUserID IS NOT NULL
    THEN 
		insert into messageRecipient values(:new.msgID, :new.toUserID);
    END IF;
	end;
/