drop table profile cascade constraints; 
CREATE TABLE PROFILE(
	userID	varchar2(20),
	name	varchar2(20) NOT NULL,
	password	varchar2(20) NOT NULL,
	date_of_birth	date,
	lastLogin	timestamp,
	constraint profile_pk primary key (userID)
	deferrable initially immediate
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
	deferrable initially immediate,
	constraint friends2_fk foreign key (userID2) references profile(userID)
    deferrable initially immediate
);

drop table pendingFriends cascade constraints;
CREATE TABLE pendingFriends(
	fromID	varchar2(20),
	toID	varchar2(20),
	message	varchar2(200),
	constraint pendingF_pk	primary key (fromID, toID)
	deferrable initially immediate,
	constraint pendingF1_fk foreign key (fromID) references profile(userID)
	deferrable initially immediate,
	constraint pendingF2_fk foreign key (toID) references profile(userID)
	deferrable initially immediate
);

drop table pendingGroupmembers cascade constraints; 
CREATE TABLE pendingGroupmembers(
	gID	varchar2(20),
	userID	varchar2(20),
	message	varchar2(20),
	constraint pendingG_pk primary key (gID, userID)
	deferrable initially immediate,
	constraint pendingG1_fk foreign key (gID) references groups (gID)
	deferrable initially immediate,
	constraint pendingG2_fk foreign key (userID) references profile (userID)
);




---------groupMembership--------------
drop table groupMembership cascade constraints;
create table groupMembership (
	gID varchar2(20)    
	, userID varchar2(2)
	, role varchar2(20) default 'user'
	, constraint pk_group_membership primary key (gID,userID)
	, constraint ch_role check(role in ('manager', 'user'))
	, constraint fk_group foreign key (gID) references groups (gID) deferrable initially immediate
	, constraint fk_user foreign key (userID) references profile(userID) deferrable initially immediate
);
	
	
---------groups ----------
drop table groups cascade constraints;
create table groups (
	gID varchar2(20)
	, name varchar2(50) not null 
	, description varchar2(200) 
	, constraint pk_groups primary key (gID) deferrable initially immediate
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
	, constraint fk_from_profile foreign key (fromID) references profile(userID) deferrable initially immediate
	, constraint fk_toUser_profile foreign key (toUserID) references profile(userID) deferrable initially immediate
	, constraint fk_togroup_groups foreign key (toGroupID) references groups (gID) deferrable initially immediate
);

--------messageRecipient--------
--------------------------------
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
