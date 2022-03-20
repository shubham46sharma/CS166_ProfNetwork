/*#Group 24:
#Shreya Godishala-862313765
#Shubham Sharma- 862253820
*/
CREATE INDEX usr_name ON USR(UserId);
CREATE INDEX message_name ON MESSAGE(msgId);
CREATE INDEX work_name ON WORK_EXP(userId,company,role,startDate));
CREATE INDEX connection_name ON CONNECTION_USR(userId,connectionId);
CREATE INDEX edu_name ON EDUCATIONAL_DETAILS(userId,major,degree);

