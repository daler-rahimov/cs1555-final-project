CREATE TABLE PROFILE(
	userID	varchar2(20),
	name	varchar2(20) NOT NULL,
	password	varchar2(20) NOT NULL,
	date_of_birth	date,
	lastLogin	timestamp
	constraint profile_pk primary key (userID)
	deferrable initially immediate
);

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