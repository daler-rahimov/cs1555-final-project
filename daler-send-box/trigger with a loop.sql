create or replace trigger sendMessagetoGroup
    after insert on messages 
    for each row
    
    declare 
    cursor resipUser is 
    select g.userID, :new.msgID as msgID
    from groupmembership g
    where g.gID = :new.toGroupID;
    
    begin 
      if :new.toGroupID is not null then
        for u in resipUser LOOP
            insert into messageRecipient (msgID, userID)
            values (u.msgID, u.userID);
        END LOOP;
      end if;
    end;
/