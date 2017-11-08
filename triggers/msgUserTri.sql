create or replace trigger sendMessageToUser
	after insert on messages
	for each row 
	begin
		insert into messageRecipient values(:new.msgID, :new.toUserID);
	end;
/
