# cs1555-final-project

## Phase 1 Instruction

In order to get a database with all requirements for phase 1 do the following in sqlplus (all mentioned files are in "Phase 1" folder):

  - SQL>start SocialPanther-db
  - SQL>start AllInserts.sql
  
## Phase 2 Instruction
Here is how the methods provided in the specification is been divided into class (each class has corresponding test classes): 
  - Profile:
     - dropUser(String userID)
	 - threeDegress()
     - searchUser()
     - login()
     - logout(String userID)
	 - createUser()
	 
  - Messages:
     - sendMessageToUser (String userID)
	 - sendMessageToGroup (String userID)
	 - displayMessages (String userID)
	 - displayNewMessages (String userID)
	 - topMessages()
  
  - Friends:
     - initiateFriedship(String userID)
     - confirmFriedship(String userID)	
     - displayFrieds(String userID)

  - Group:
     - createGroup(String userID)
	 - initiateAddingGroup(String userID)

## Phase 3 Instruction



## Application Assumptions

  - topMessages() counts all messages sent and received by the users. Top users who had highest number of sent + received messages.
  
## Data model/design Assumptions

  - The b date is not requried 
  - Any entery in friends table has 2 way friendship. E.g. Bob -> Mary and Mary -> Bob 