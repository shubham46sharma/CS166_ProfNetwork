/*Group 24:
Shreya Godishala-862313765
Shubham Sharma-  862253820
*/
 862253820
DROP TABLE IF EXISTS WORK_EXPR;
DROP TABLE IF EXISTS EDUCATIONAL_DETAILS;
DROP TABLE IF EXISTS MESSAGE;
DROP TABLE IF EXISTS CONNECTION_USR;
DROP TABLE IF EXISTS USR;

CREATE TABLE USR(
	userId varchar(30) UNIQUE NOT NULL, 
	password varchar(30) NOT NULL,
	email text NOT NULL,
	name char(50),
	dateOfBirth date,
	Primary Key(userId));

CREATE TABLE WORK_EXPR(
	userId char(30) NOT NULL, 
	company char(50) NOT NULL, 
	role char(50) NOT NULL,
	location char(50),
	startDate date,
	endDate date,
	FOREIGN KEY(userId) REFERENCES USR,
	PRIMARY KEY(userId,company,role,startDate));

CREATE TABLE EDUCATIONAL_DETAILS(
	userId char(30) NOT NULL, 
	instituitionName char(50) NOT NULL, 
	major char(50) NOT NULL,
	degree char(50) NOT NULL,
	startdate date,
	enddate date,
	FOREIGN KEY(userId) REFERENCES USR,
	PRIMARY KEY(userId,major,degree));

CREATE TABLE MESSAGE(
	msgId integer UNIQUE NOT NULL, 
	senderId char(30) NOT NULL,
	receiverId char(30) NOT NULL,
	contents char(500) NOT NULL,
	sendTime timestamp,
	deleteStatus integer,
	status char(30) NOT NULL,
	PRIMARY KEY(msgId));

CREATE TABLE CONNECTION_USR(
	userId char(30) NOT NULL, 
	connectionId char(30) NOT NULL, 
	status char(30) NOT NULL,
	PRIMARY KEY(userId,connectionId));