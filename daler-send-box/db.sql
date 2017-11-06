
---------profiel---------------

---------groupMembership--------------
drop table groupMembership cascade constraints;
create table groupMembership (
	gID varchar2(20)    
	, userID varchar2(2)
	, role varchar2(20) default 'user'
	, constraint pk_group_membership primary key (gID,userID)
	, constraint ch_role check(role in ('manager', 'user'))
	--add constraint gID
	--add constraint for userID
);
	
---------groups ----------
drop table groups cascade constraints;
create table groups (
	gID varchar2(20)
	, name varchar2(50) not null 
	, description varchar2(200) 
	, constraint pk_groups primary key (gID)
);


---------messages---------
drop table messages cascade CONSTRAINTS;
create table messages (
	msgID varchar2(20)
	, fromID varchar2(20)
	, message varchar2(200)
	, toGroupID varchar2(20)
	, toUserID varchar2(20)
	, dateSent date not null 
	, constraint pk_messages primary key (msgID)
	--add constraint for fromID
	--add constraint for toGroupID
	--add constraint for toUserID
);

	