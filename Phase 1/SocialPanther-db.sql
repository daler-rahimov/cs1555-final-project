drop table profile cascade constraints; 
CREATE TABLE PROFILE(
	userID	varchar2(20),
	name	varchar2(20) NOT NULL,
	email	varchar2(20),
	password	varchar2(20) NOT NULL,
	date_of_birth	date,
	lastLogin	timestamp,
	constraint profile_pk primary key (userID)
		deferrable initially immediate
	--make email unique??
);

---------groups ----------
drop table groups cascade constraints;
create table groups (
	gID varchar2(20)
	, name varchar2(50) not null 
	, description varchar2(200)
	, gLimit number
	, constraint pk_groups primary key (gID) deferrable initially immediate
);


drop table friends cascade constraints;
CREATE TABLE friends(
	userID1	varchar2(20),
	userID2 varchar2(20),
	JDate	date	NOT NULL,
	message	varchar2(200),
	constraint friends_pk primary key (userID1, userID2)
		deferrable initially immediate,
	constraint friends1_fk foreign key (userID1) references profile(userID)
		on delete cascade deferrable initially immediate,
	constraint friends2_fk foreign key (userID2) references profile(userID)
		on delete cascade deferrable initially immediate
);

drop table pendingFriends cascade constraints;
CREATE TABLE pendingFriends(
	fromID	varchar2(20),
	toID	varchar2(20),
	message	varchar2(200),
	constraint pendingF_pk	primary key (fromID, toID)
		deferrable initially immediate,
	constraint pendingF1_fk foreign key (fromID) references profile(userID)
		on delete cascade deferrable initially immediate,
	constraint pendingF2_fk foreign key (toID) references profile(userID)
		on delete cascade deferrable initially immediate
);


drop table pendingGroupmembers cascade constraints; 
CREATE TABLE pendingGroupmembers(
	gID	varchar2(20),
	userID	varchar2(20),
	message	varchar2(200),
	constraint pendingG_pk primary key (gID, userID)
		deferrable initially immediate,
	constraint pendingG1_fk foreign key (gID) references groups (gID)
		on delete cascade deferrable initially immediate,
	constraint pendingG2_fk foreign key (userID) references profile (userID)
		on delete cascade deferrable initially immediate
);




---------groupMembership--------------
drop table groupMembership cascade constraints;
create table groupMembership (
	gID varchar2(20)    
	, userID varchar2(2)
	, role varchar2(20) default 'user'
	, constraint pk_group_membership primary key (gID,userID)
	, constraint ch_role check(role in ('manager', 'user'))
	, constraint fk_group foreign key (gID) references groups (gID) on delete cascade deferrable initially immediate 
	, constraint fk_user foreign key (userID) references profile(userID) on delete cascade deferrable initially immediate 
);
	
	

---------messages---------
drop table messages cascade constraints;
create table messages (
	msgID varchar2(20)
	, fromID varchar2(20)
	, message varchar2(200)
	, toGroupID varchar2(20)
	, toUserID varchar2(20)
	, dateSent date not null 
	, constraint pk_messages primary key (msgID)
	, constraint fk_from_profile foreign key (fromID) references profile(userID)  on delete cascade deferrable initially immediate
	, constraint fk_toUser_profile foreign key (toUserID) references profile(userID) on delete cascade deferrable initially immediate 
	, constraint fk_togroup_groups foreign key (toGroupID) references groups (gID) on delete cascade deferrable initially immediate 
);

--------messageRecipient---------
---------------------------------
/*
Assumption:
	1. For any message that any user receives there will be an entery added to this table 
	even if a message sent through group messaging 
	2. No message can be send to the same user twice, hence primary key (msgID, userID)  
*/
------------------------------ 
drop table messageRecipient cascade constraints ;
create table messageRecipient(
	msgID varchar2(20)
	, userID varchar2(20)
	, constraint pk_messageRecipient primary key (msgID, userID)
	, constraint fk_msgID foreign key (msgID) references messages(msgID) deferrable initially immediate
);


-------------****Triggers****-------------------
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

/*
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
*/

-------------send msg user trigger------------------
/*

*/
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

--groupRequestMsg

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
		values (nmsgID, :new.userID, :new.message, :new.gID, current_date);

		--not sure if this is needed, might be covered by the groupMsg trigger already
        --insert into messageRecipient (msgID, userID)
        --SELECT :new.msgID, g.userID
        --FROM groupMembership g
        --WHERE g.gID = :new.toGroupID;
    END IF;
	end;
/


--Friend Request Message

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


-------------****EndTriggers***-----------------