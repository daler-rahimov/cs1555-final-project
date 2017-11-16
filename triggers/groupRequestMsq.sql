create or replace trigger groupReqMsg
	after insert on pendingGroupmembers
	for each row
	DECLARE
		nmsgID varchar(20);
    begin
    IF :new.message IS NOT NULL
    THEN 	
		SELECT to_char(count(msgID) + 1) INTO nmsgID
		FROM messages;
		
		insert into messages (msgID, fromID, message, toGroupID, dateSent)
		values (nmsgID, :new.userID, :new.message, :new.toGroupID, current_date);

		--not sure if this is needed, might be covered by the groupMsg trigger already
        --insert into messageRecipient (msgID, userID)
        --SELECT :new.msgID, g.userID
        --FROM groupMembership g
        --WHERE g.gID = :new.toGroupID;
    END IF;
	end;
/