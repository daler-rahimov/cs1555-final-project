# cs1555-final-project

## Phase 1 Instruction

In order to get a database with all requirements for phase 1 do the following in sqlplus (all mentioned files are in "Phase 1" folder):

  - SQL>start SocialPanther-db
  - SQL>start AllInserts.sql
  
## Phase 2 Instruction
Here is how the methods provided in the specification is been divided into classes (each class has corresponding test class): 
  - Profile: (to run: javac ProfileTest.java; java ProfileTest) 
     - dropUser(String userID)
	 - threeDegress()
     - searchUser()
     - login()
     - logout(String userID)
	 - createUser()
	 
  - Message: (to run: javac MessageTest.java; java MessageTest)
     - sendMessageToUser (String userID)
	 - sendMessageToGroup (String userID)
	 - displayMessages (String userID)
	 - displayNewMessages (String userID)
	 - topMessages()
  
  - Friends: (to run: javac Friends.java; java Friends)
     - initiateFriendship(String userID)
     - confirmFriendship(String userID)	
     - displayFriends(String userID)

  - Group: (to run: javac Group.java; java Group)
     - createGroup(String userID)
	 - initiateAddingGroup(String userID)

## Phase 3 Instruction:
The automated test class is in MainTest.java all the inputs are hardcoded into the source code. All test output will be writen to stdout. To Run 
   - javac MainTest.java
   - java MainTest
   - For some of the tests, function will show error catching capabilities if the program is run twice without resetting the database.
   - All classes needed to run MainTest are in phase 2 folder


## Application Assumptions

  - topMessages() counts all messages sent and received by the users. Top users who had highest number of sent + received messages.
  
## Data model/design Assumptions

  - The b date is not requried 
  - Any entry in friends table has 2 way friendship. E.g. Bob -> Mary and Mary -> Bob
  - Messages with friend and group requests are not added to Message Table