/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

 /*Group 24:
Shreya Godishala-862313765
Shubham Sharma- 862253820
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProfNetwork {

    // reference to physical database connection.
    private Connection _connection = null;

    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

    /**
     * Creates a new instance of ProfNetwork
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

        System.out.print("Connecting to database...");
        try{
            // constructs the connection URL
            String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
            System.out.println ("Connection URL: " + url + "\n");

            // obtain a physical connection
            this._connection = DriverManager.getConnection(url, user, passwd);
            System.out.println("Done");
        }catch (Exception e){
            System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
            System.out.println("Make sure you started postgres on this machine");
            System.exit(-1);
        }//end catch
    }//end ProfNetwork

    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate (String sql) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();

        // issues the update instruction
        stmt.executeUpdate (sql);

        // close the instruction
        stmt.close ();
    }//end executeUpdate

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);

        /*
         ** obtains the metadata object for the returned result set.  The metadata
         ** contains row and column info.
         */
        ResultSetMetaData rsmd = rs.getMetaData ();
        int numCol = rsmd.getColumnCount ();
        int rowCount = 0;

        // iterates through the result set and output them to standard out.
        boolean outputHeader = true;
        while (rs.next()){
            if(outputHeader){
                for(int i = 1; i <= numCol; i++){
                    System.out.print(rsmd.getColumnName(i) + "\t");
                }
                System.out.println();
                outputHeader = false;
            }
            for (int i=1; i<=numCol; ++i)
                System.out.print (rs.getString (i) + "\t");
            System.out.println ();
            ++rowCount;
        }//end while
        stmt.close ();
        return rowCount;
    }//end executeQuery
    public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);

        /*
         ** obtains the metadata object for the returned result set.  The metadata
         ** contains row and column info.
         */
        ResultSetMetaData rsmd = rs.getMetaData ();
        int numCol = rsmd.getColumnCount ();
        int rowCount = 0;

        // iterates through the result set and saves the data returned by the query.
        boolean outputHeader = false;
        List<List<String>> result  = new ArrayList<List<String>>();
        while (rs.next()){
            List<String> record = new ArrayList<String>();
            for (int i=1; i<=numCol; ++i)
                record.add(rs.getString (i));
            result.add(record);
        }//end while
        stmt.close ();
        return result;
    }//end executeQueryAndReturnResult

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);

        int rowCount = 0;

        // iterates through the result set and count nuber of results.
        if(rs.next()){
            rowCount++;
        }//end while
        stmt.close ();
        return rowCount;
    }
    public int getCurrSeqVal(String sequence) throws SQLException {
        Statement stmt = this._connection.createStatement ();

        ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
        if (rs.next())
            return rs.getInt(1);
        return -1;
    }

    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup(){
        try{
            if (this._connection != null){
                this._connection.close ();
            }//end if
        }catch (SQLException e){
            // ignored.
        }//end try
    }//end cleanup

    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
    public static void main (String[] args) {
        if (args.length != 3) {
            System.err.println (
                    "Usage: " +
                            "java [-classpath <classpath>] " +
                            ProfNetwork.class.getName () +
                            " <dbname> <port> <user>");
            return;
        }//end if

        Greeting();
        ProfNetwork esql = null;
        User_Profile prof = new User_Profile();
        User_Connect conn = new User_Connect();
        Messenger msg = new Messenger();
        try{
            // use postgres JDBC driver.
            Class.forName ("org.postgresql.Driver").newInstance ();
            // instantiate the ProfNetwork object and creates a physical
            // connection.
            String dbname = args[0];
            String dbport = args[1];
            String user = args[2];
            esql = new ProfNetwork (dbname, dbport, user, "");
            boolean keepon = true;
            while(keepon) {
                // These are sample SQL statements
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Create user");
                System.out.println("2. Log in");
                System.out.println("9. < EXIT");
                String authorisedUser = null;
                switch (readChoice()){
                    case 1: {CreateUser(esql); break;}
                    case 2: {authorisedUser = LogIn(esql); break;}
                    case 9: {keepon = false; break;}
                    default : {System.out.println("Unrecognized choice!"); break;}
                }//end switch

                if (authorisedUser != null) {
                    boolean usermenu = true;
                    while(usermenu) {
                        System.out.println("MAIN MENU");
                        System.out.println("**********************");
                        System.out.println("Pick your choice from the Menu:");
                        System.out.println("1. Update Password");
                        System.out.println("2. Search For a user Profile");
                        System.out.println("3. View Friend's Profile to Send Message or Connection Request");
                        System.out.println("4. Messaging Options");
                        System.out.println("5. Connection Options ");
                        System.out.println("*********************");
                        System.out.println("6. EXIT");
                        switch (readChoice()){
                            //case 1: FriendList(esql); break;
                            case 1: User_Profile.UpdateProfile(esql, authorisedUser); break;
                            case 2: User_Connect.Search_User(esql, authorisedUser);break;
                            case 3: User_Profile.View_Connections(esql, authorisedUser); break;
                            case 4: Messenger.Message_Options(esql, authorisedUser);break;
                            //case 3: NewMessage(esql); break;
                            //case 3: msg.SendMessage(esql, authorisedUser); break;
                            case 5: User_Connect.ConnectMenu(esql, authorisedUser); break;
                            //case 3: UserConnect.Search(esql, authorisedUser);break;
                            //case 4: Messenger.MessageService(esql, authorisedUser);break;
                            case 6: usermenu = false; break;
                            default : System.out.println("Invalid!"); break;
                        }
                    }
                }
            }//end while
        }catch(Exception e) {
            System.err.println (e.getMessage ());
        }finally{
            // make sure to cleanup the created table and close the connection.
            try{
                if(esql != null) {
                    System.out.print("Disconnecting from database...");
                    esql.cleanup ();
                    System.out.println("Done\n\nBye !");
                }//end if
            }catch (Exception e) {
                // ignored.
            }//end try
        }//end try
    }//end main
    public static void Greeting(){
        System.out.println(
                "\n\n*******************************************************\n" +
                        "              User Interface                         \n" +
                        "*******************************************************\n");
    }//end Greeting

    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
        int input;
        // returns only if a correct value is given.
        do {
            System.out.print("Please make your choice: ");
            try { // read the integer, parse it and break.
                input = Integer.parseInt(in.readLine());
                break;
            }catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }//end try
        }while (true);
        return input;
    }//end readChoice
    /*
     * Creates a new user with privided login, passowrd and phoneNum
     * An empty block and contact list would be generated and associated with a user
     **/
    public static void CreateUser(ProfNetwork esql){
        try{
            System.out.print("\tEnter user login: ");
            String login = in.readLine();
            System.out.print("\tEnter user password: ");
            String password = in.readLine();
            System.out.print("\tEnter user email: ");
            String email = in.readLine();
            System.out.print("\tEnter user Full Name: ");
            String name = in.readLine();
            System.out.print("\tEnter user Date Of Birth: ");
            String birthdate = in.readLine();

            //Creating empty contact\block lists for a user
            String query = String.format("INSERT INTO USR (userId, password, email,name,dateOfBirth) VALUES ('%s','%s','%s','%s','%s')", login, password, email,name,birthdate);

            esql.executeUpdate(query);
            System.out.println ("User successfully created!");
        }catch(Exception e){
            System.err.println (e.getMessage ());
        }
    }//end

    public static String LogIn(ProfNetwork esql){
        try{
            System.out.print("\tEnter user login: ");
            String login = in.readLine();
            System.out.print("\tEnter user password: ");
            String password = in.readLine();

            String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
            int userNum = esql.executeQuery(query);
            if (userNum > 0)
                return login;
            return null;
        }catch(Exception e){
            System.err.println (e.getMessage ());
            return null;
        }
    }//end
   /* public static void FriendList(ProfNetwork esql){

    }
    /*public static void UpdateProfile(ProfNetwork esql){

    }*/
    public static void NewMessage(ProfNetwork esql){

    }
    public static void SendRequest(ProfNetwork esql){

    }
    // Rest of the functions definition go in here

}//end ProfNetwork
class User_Profile<getChoice> {
    public static Messenger msg = new Messenger();
    public static User_Connect conn = new User_Connect();
    public static void UpdateProfile(ProfNetwork esql, String c_user){
        boolean usermenu = true;
        while(usermenu){
            System.out.println("1.Update password");
            System.out.println("2.<Exit");
            switch(ProfNetwork.readChoice()){
                case 1:{update_Password(esql, c_user);break;}
                case 2:{usermenu = false;}
            }
        }
    }
    public static void update_Password(ProfNetwork esql, String c_user){
        String pwd;
        try{
            System.out.print("\t Please Enter a New Password:");
            pwd = ProfNetwork.in.readLine();
            String query = String.format("UPDATE usr set password ='%s' WHERE userId = '"+c_user+"'", pwd);
            esql.executeUpdate(query);
            System.out.println("Password Updated Successful!");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public static void View_User_Profile(ProfNetwork esql,String c_user,String u_name)
    {
        boolean connection= false;
        List<List<String>> result =  new ArrayList<List<String>>();
        String query = ("SELECT connectionId AS userId FROM connection_usr WHERE userId='"+u_name+"'AND connectionId ='"+c_user+"' AND status='Accept' UNION ALL SELECT userId FROM connection_usr WHERE userId = '"+c_user+"' AND connectionId ='" +u_name+ "' AND status ='Accept'");
        try{
            if(esql.executeQuery(query)>0){
                connection=true;
                String uName = u_name.trim();
                query=String.format("SELECT name,dateOfBirth FROM usr WHERE userId='"+uName+"'");
                try {
                    result = esql.executeQueryAndReturnResult(query);
                    System.out.println("Name : " +result.get(0).get(0)+"");
                    System.out.println("Date Of Birth :" +result.get(0).get(1)+"\n");
                }catch(Exception e){
                    System.err.println(e.getMessage());
                }
            }
            else{
                System.out.println("Userid:"+u_name+"");
            }
        }catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
        result.clear();
        query=String.format("SELECT company,role,location,startDate,endDate FROM work_expr WHERE userId='"+u_name+"'");
        int count=1;
        try{
            System.out.println("Work Experience:");
            result=esql.executeQueryAndReturnResult(query);
            if(result.isEmpty()){
                System.out.println("None");
            }
            else
            {
                for(int i=0;i<result.size();i++){
                    System.out.print("\t " +count+" . ");
                    for(int j=0;j<result.get(i).size();j++)
                    {
                        System.out.print(""+result.get(i).get(j)+"");
                    }
                    System.out.println();
                    count++;
                }
                System.out.println();
            }
        }catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
        count=1;
        result.clear();
        query=String.format("SELECT instituitionName,major,degree, startdate,enddate FROM educational_details WHERE userId = '"+u_name+"'");
        try
        {
            System.out.println("Education: ");
            result = esql.executeQueryAndReturnResult(query);
            if(result.isEmpty()){
                System.out.println("None\n");
            }
            else
            {
                for(int i=0;i<result.size();i++) {
                    System.out.print("\t " +count+ ".");
                    for (int j=0;j<result.get(i).size();j++){
                        System.out.print(""+result.get(i).get(j)+"");
                        System.out.print("");
                    }
                    count++;
                    System.out.println();
                }
                System.out.println();
            }
        }catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
        boolean getChoice= true;
        while(getChoice){
            System.out.println("1.View Users Connections");
            System.out.println("2.Send Message");
            query = ("SELECT connectionId AS userId FROM connection_usr WHERE userId='"+u_name+"'AND connectionId ='"+u_name+"' AND status='Accept' UNION ALL SELECT userId FROM connection_usr WHERE userId = '"+c_user+"' AND connectionId ='" +u_name+ "' AND status ='Accept'");
            boolean connReq=false;
            try{
                if(esql.executeQuery(query)>0){
                    System.out.println("3.Send Connection Request:");
                    connReq=true;
                }
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
            System.out.println("9.Return");
            switch(ProfNetwork.readChoice()){
                case 1:ViewUserConnections(esql,c_user,u_name,connection);break;
                case 2: Messenger.Send_Message_Profile(esql,c_user,u_name);break;
                case 3: if(connReq){
                    User_Connect.Connection_Request(esql,c_user,u_name);
                }break;
                case 9: getChoice=false;break;
                default: System.out.println("invalid choice.Please Try Again");
            }
        }
    }
    public static void View_Connections(ProfNetwork esql, String c_user){
        String query = String.format("SELECT connectionId FROM connection_usr WHERE userid = '"+c_user+"' AND status = 'Accept'");
        List<List<String>> result = new ArrayList<List<String>>();
        try{
            result = esql.executeQueryAndReturnResult(query);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

        boolean getChoice = true;
        System.out.println("\f\f\f\f\f\f\f\f\f\f\f\f\f\f");
        while(getChoice){
            System.out.println(""+c_user+"'s connection list:");
            int count = 1;
            int usrChoice = 0;
            if(result.size() > 0){
                for(int i = 0; i <result.size(); i++){
                    System.out.println(""+count+". " + ""+result.get(i).get(0)+"");
                    count++;
                }
            } else{
                System.out.println("Only has you as a connection.");
            }
            System.out.println();
            System.out.println("\n1. View Profile of Connection");
            System.out.println("2. Send message to a connection");
            System.out.println("---------");
            System.out.println("3. Exit\n");
            switch(esql.readChoice()){
                case 1: System.out.print("Please enter the number of the connection you want to view: ");
                    try{
                        usrChoice = Integer.parseInt(esql.in.readLine().trim()) - 1;
                    } catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                    View_User_Profile(esql, c_user, result.get(usrChoice).get(0)); break;
                case 2: System.out.print("Please enter the number of the connection you want to send a message: ");
                    try{
                        usrChoice = Integer.parseInt(esql.in.readLine().trim()) - 1;
                    } catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                    msg.Send_Message_Profile(esql, c_user, result.get(usrChoice).get(0)); break;
                case 3: getChoice = false; break;
                default: System.out.println("Invalid choice. Please try again.");
                    try{
                        String errCtch = esql.in.readLine();
                    } catch (Exception e){
                    }
            }
        }
    }
    public static void ViewUserConnections(ProfNetwork esql, String c_user, String u_name, boolean connection){
        String query = String.format("SELECT connectionId AS userid FROM connection_usr WHERE userid = '"+u_name+"' AND connectionId <> '"+c_user+"' AND status = 'Accept' UNION ALL SELECT userid FROM connection_usr WHERE  userid <> '"+c_user+"' AND connectionId = '"+u_name+"' AND status = 'Accept'");
        List<List<String>> result = new ArrayList<List<String>>();
        try{
            result = esql.executeQueryAndReturnResult(query);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

        boolean getChoice = true;
        System.out.println("\f\f\f\f\f\f\f\f\f\f\f\f\f\f");
        while(getChoice){
            System.out.println(""+u_name+"'s connection list:");
            int count = 1;
            int usrChoice = 0;
            if(result.size() > 0){
                for(int i = 0; i < result.size(); i++){
                    System.out.println(""+count+". " + ""+result.get(i).get(0)+"");
                    count++;
                }
            } else{
                System.out.println("Only has you as a connection.");
            }
            System.out.println();
            System.out.println("\n1. View connection profile");
            System.out.println("2. Send message");
            System.out.println("---------");
            System.out.println("9. Return to previous menu\n");
            switch(ProfNetwork.readChoice()){
                case 1: System.out.print("Please enter the number of the connection you want to view: ");
                    try{
                        usrChoice = Integer.parseInt(ProfNetwork.in.readLine().trim()) - 1;
                    } catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                    View_User_Profile(esql, c_user, result.get(usrChoice).get(0)); break;
                case 2: System.out.print("Please enter the number of the connection you want to send a message: ");
                    try{
                        usrChoice = Integer.parseInt(ProfNetwork.in.readLine().trim()) - 1;
                    } catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                    Messenger.Send_Message_Profile(esql, c_user, result.get(usrChoice).get(0)); break;
                case 9: getChoice = false; break;
                default: System.out.println("Invalid choice. Please try again.");
                    try{
                        String errCtch = ProfNetwork.in.readLine();
                    } catch (Exception e){
                    }
            }
        }
    }
}
class User_Connect{

    public static User_Profile prof = new User_Profile();
    public static Messenger msg = new Messenger();
    public static void ConnectMenu(ProfNetwork esql, String c_user){
        boolean getChoice = true;
        while(getChoice){
            System.out.println("\nConnection Options");
            System.out.println("1. View Connections of the User");
            System.out.println("2. Accept or Decline Connection Requests");
            System.out.println("---------");
            System.out.println("9.Exit\n");

            switch(ProfNetwork.readChoice()){
                case 1: user_connections(esql, c_user); break;
                case 2: View_Connection_Request(esql, c_user); break;
                //case 3: NonProfileRequest(esql, c_user); break;
                case 9: getChoice = false; break;
                default: System.out.println("Invalid");
            }
        }
    }
    public static void user_connections(ProfNetwork esql, String c_user){
        List<List<String>> result = new ArrayList<List<String>>();
        try{
            String query = String.format("SELECT connectionId AS userid FROM connection_usr WHERE userID = '%s' AND status = '%s' UNION ALL SELECT userid FROM connection_usr WHERE connectionId = '%s' AND status= '%s'",
                    c_user, "Accept", c_user, "Accept");
            result = esql.executeQueryAndReturnResult(query);
            if(result.isEmpty()){
                System.out.println("There are no connections.Please send new connection requests.\n");
            }
            else{
                boolean getChoice = true;
                while(getChoice){
                    System.out.println("\nConnections of the User: ");
                    int count = 1;
                    for(int i = 0; i < result.size(); i++){
                        System.out.println(""+count+". " + ""+result.get(i).get(0)+"");
                        count++;
                    }
                    System.out.println("1. View Profile of the connection");
                    System.out.println("2. Send Message to the connection");
                    System.out.println("---------");
                    System.out.println("9. Exit\n");

                    int usrChoice = 0;
                    switch(ProfNetwork.readChoice()){
                        case 1: System.out.print("Please Enter The connection Number :");
                            usrChoice = Integer.parseInt(ProfNetwork.in.readLine().trim()) - 1;
                            System.out.println();
                            User_Profile.View_User_Profile(esql, c_user, result.get(usrChoice).get(0));
                            break;
                        case 2: System.out.println("Send Message to a Connection: ");
                            usrChoice = Integer.parseInt(ProfNetwork.in.readLine().trim()) -1;
                            System.out.println();
                            Messenger.Send_Message_Profile(esql, c_user, result.get(usrChoice).get(0));
                            break;
                        case 9: getChoice = false; break;
                        default: System.out.println("Invalid!.");

                    }
                }
            }

        }catch(Exception e){
            System.err.println (e.getMessage());
        }

    }
    public static void View_Connection_Request(ProfNetwork esql, String c_user){
        List<List<String>> result = new ArrayList<List<String>>();
        try{
            String query = String.format("SELECT userid FROM connection_usr WHERE connectionId = '"+c_user+"' AND status = 'Request'");
            result = esql.executeQueryAndReturnResult(query);
            if(result.isEmpty()){
                System.out.println("Empty connection Request pool.");
            } else{
                boolean getChoice = true;
                while(!result.isEmpty() && getChoice){
                    int count = 1;
                    System.out.println("Connection Requests: ");
                    for(int i = 0; i < result.size(); i++){
                        System.out.println(""+count+". " + ""+result.get(i).get(0)+"");
                        count++;
                    }
                    System.out.println("\n1.Accept Connection Request");
                    System.out.println("2.Reject Connection Request");
                    System.out.println("---------");
                    System.out.println("9.Exit\n");

                    int reqChoice = 0;
                    switch(ProfNetwork.readChoice()){
                        case 1: System.out.print("Please enter the request to accept: ");
                            try{
                                reqChoice = Integer.parseInt(ProfNetwork.in.readLine().trim()) - 1;
                                query = String.format("UPDATE connection_usr SET status = 'Accept' WHERE userid = '"+result.get(reqChoice).get(0)+"' AND connectionId = '"+c_user+"'");
                                try{
                                    esql.executeUpdate(query);
                                    result.remove(reqChoice);
                                } catch (Exception e){
                                    System.err.println(e.getMessage());
                                }
                            } catch (Exception e){
                                System.err.println(e.getMessage());
                            }
                            break;
                        case 2: System.out.print("Please enterthe request to reject: ");
                            try{
                                reqChoice = Integer.parseInt(ProfNetwork.in.readLine().trim()) - 1;
                                query = String.format("UPDATE connection_usr SET status = 'Reject' WHERE userid = '"+result.get(reqChoice).get(0)+"' AND connectionId = '"+c_user+"'");
                                try{
                                    esql.executeUpdate(query);
                                    result.remove(reqChoice);
                                } catch (Exception e){
                                    System.err.println(e.getMessage());
                                }
                            } catch (Exception e){
                                System.err.println(e.getMessage());
                            }
                            break;
                        case 9: getChoice = false; break;
                        default: System.out.println("Invalid!.");
                    }
                    System.out.println();
                }
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    public static void Connection_Request(ProfNetwork esql, String c_user, String userReq){
        if(Connection_Depth_Check(esql, c_user, userReq)){
            System.out.println("Request being sent to: '"+userReq+"'");
            String query = String.format("INSERT INTO connection_usr (userId, connectionId, status) " + "VALUES('"+c_user+"', '"+userReq+"', 'Request')");
            try{
                esql.executeUpdate(query);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        } else{
            System.out.println("Cant request the user.");
        }
    }
    public static boolean Connection_Depth_Check(ProfNetwork esql, String c_user, String userReq){
        boolean status = false;
        List<List<String>> result = new ArrayList<List<String>>();
        String query = String.format("SELECT count(*) FROM connection_usr WHERE userid = '%s' AND status = 'Accept' OR connectionId = '%s' AND status = 'Accept'", c_user, c_user);
        try{
            result = esql.executeQueryAndReturnResult(query);
            int count = Integer.parseInt(result.get(0).get(0).trim());
            if(count > 4){
                try{
                    query = String.format("SELECT * FROM %s WHERE userid = '%s'", "\"" + c_user + "\"", userReq);
                    int size = esql.executeQuery(query);
                    status = size > 0;
                } catch (Exception e){
                    System.err.println(e.getMessage());
                    status = false;
                }
            } else {
                status = true;
            }
        } catch (Exception e){
            status = false;
            System.err.println(e.getMessage());
        }
        return status;
    }
    public static void Search_User(ProfNetwork esql, String c_user){
        List<List<String> > result = new ArrayList<List<String> >();
        try{
            System.out.println("Please Enter User's First Name: ");
            String First_Name = ProfNetwork.in.readLine();
            try{
                System.out.println("Please Enter User's Last Name:  ");
                String Last_Name = ProfNetwork.in.readLine();
                String Full_Name = ""+First_Name+" "+Last_Name;
                String query = String.format("SELECT userId FROM usr WHERE name = '"+Full_Name+"'");
                try{
                    result = esql.executeQueryAndReturnResult(query);
                    if(result.isEmpty()){
                        System.out.println("No User Found!");
                    }
                    else{
                        int c = 1;
                        for(int i=0;i<result.size();i++){
                            System.out.println("\n\t"+c+". "+result.get(i).get(0)+"");
                            c++;
                        }
                    }
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
                int userCh = 1;
                System.out.println("\n");
                System.out.println("1. View the Profile of the User");
                // System.out.println("2. Send Message");
                System.out.println("2. Return\n");
                switch(ProfNetwork.readChoice()){
                    case 1:
                        System.out.println("View User Profile Number:");
                        try{
                            userCh=Integer.parseInt(ProfNetwork.in.readLine().trim())-1;
                            User_Profile.View_User_Profile(esql,c_user,result.get(userCh).get(0));
                        }catch(Exception e){
                        }break;
                    case 3: Search_User(esql,c_user);break;
                    case 9: break;
                    default: System.out.println("Invalid input.Please try again. ");
                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
class Messenger{
    public static void Message_Options(ProfNetwork esql, String c_user){
        boolean menuOn = true;
        System.out.println("\f\f\f\f\f\f\f\f\f\f\f\f\f\f");
        while(menuOn){
            System.out.println("\n Message Options");
            System.out.println("1. Read Message");
            System.out.println("2. Send Message");
            System.out.println("3:Exit\n");
            switch(ProfNetwork.readChoice()){
                case 1: Read_Message(esql, c_user);
                    break;
                case 2: Send_Message(esql, c_user);
                    break;
                case 3: menuOn = false; break;
                default: System.out.println("\nERROR\n");
            }
        }
    }
    public static void Read_Message(ProfNetwork esql, String c_user){
        boolean getChoice = true;
        System.out.println("\f\f\f\f\f\f\f\f\f\f\f\f\f\f\f\f\f");
        while(getChoice){
            System.out.println("\nRead Messages");
            System.out.println("---------");
            System.out.println("1. View any new messages");
            System.out.println("2. Read a  Message");
            System.out.println("3. View all received messages");
            System.out.println("4. Delete a received message");
            System.out.println("---------");
            System.out.println("5.Exit\n");
            switch(ProfNetwork.readChoice()){
                case 1: try{
                    String query = String.format("SELECT msgId, senderId FROM message WHERE receiverId = '%s' AND status = '%s' AND (deleteStatus = '0' OR deleteStatus = '1')", c_user, "Delivered");

                    int result = esql.executeQueryAndPrintResult(query);
                    if(result < 1){
                        System.out.println("There are no unread messages.");
                    }
                }catch(Exception e){
                    System.err.println(e.getMessage());
                }
                    break;
                case 2: ReadMessage(esql, c_user);
                    break;
                case 3: try{
                    String query = String.format("SELECT msgId,senderId  FROM message WHERE receiverId = '%s' AND (status = '%s' OR status = '%s') AND (deleteStatus = '0' OR deleteStatus ='1')", c_user, "Delivered", "Read");
                    int result = esql.executeQueryAndPrintResult(query);
                    if(result < 1){
                        System.out.println("There are no messages in your inbox.");
                    }
                }catch(Exception e){
                    System.err.println(e.getMessage());
                }
                    break;
                case 4: Delete_Message(esql, c_user, "read");
                    break;
                case 5: getChoice = false;
                    break;
                default: System.out.println("\nInvalid!.\n");
            }
        }
    }
    public static void ReadMessage(ProfNetwork esql, String c_user){
        System.out.print("\t\nPlease enter the message id you would like to read: ");
        try{
            String input = ProfNetwork.in.readLine();
            int msgId= Integer.parseInt(input.trim());
            try{
                System.out.println();
                String query = String.format("SELECT contents FROM message WHERE msgId = '%s' AND receiverId = '%s' AND (deleteStatus = '0' OR deleteStatus = '1')", msgId, c_user);
                int result = esql.executeQueryAndPrintResult(query);
                if(result < 1){
                    System.out.println("No message found with that id. Please try again.");
                }
                else{
                    try{
                        query = String.format("UPDATE message SET status = '%s' WHERE msgId = '%s'", "Read", msgId);
                        esql.executeUpdate(query);
                    }catch (Exception e){
                    }
                }
            }catch (Exception e){
            }
        }catch (Exception e){
            System.out.println("Invalid input. The message id is an integer. Please try again");
        }
    }
    public static void Send_Message(ProfNetwork esql, String c_user){
        boolean getChoice = true;
        System.out.println("\f\f\f\f\f\f\f\f\f\f\f\f\f\f\f");
        while(getChoice){
            System.out.println("\nSend Messages");
            System.out.println("---------");
            System.out.println("1. Send a Message");
            System.out.println("2. Sent Messages");
            System.out.println("3. View a Sent Message");
            System.out.println("4. Delete Sent Message");
            System.out.println("---------");
            System.out.println("9. Exit\n");

            switch(ProfNetwork.readChoice()){
                case 1: SendMessage(esql, c_user);
                    break;
                case 2: ListSentMessages(esql, c_user);
                    break;
                case 3: ViewSentMessage(esql, c_user);
                    break;
                case 4: Delete_Message(esql, c_user, "send");
                    break;
                case 9: getChoice = false;
                    break;
                default: System.out.println("Invalid Choice. Please try again.");
            }
        }
    }
    public static void SendMessage(ProfNetwork esql, String c_user){
        try{
            System.out.print("Please enter the userid of the user you want to send the message: ");
            String receiveid = ProfNetwork.in.readLine();
            System.out.print("\nPlease enter the message you want to send: ");
            String contents = ProfNetwork.in.readLine();
            try{
                String query2 = String.format("SELECT COUNT(*) FROM MESSAGE");
                int count = esql.executeQuery(query2);
                System.out.println(""+count);
                String query = String.format("INSERT INTO message (senderId, receiverId, contents, deleteStatus, status) " + "VALUES('"+c_user+"', '"+receiveid+"', '"+contents+"', 0, 'Delivered')");
                esql.executeUpdate(query);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
            System.out.println("Message has been Inserted!");
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    public static void Send_Message_Profile(ProfNetwork esql, String c_user, String receiverId){
        System.out.println("\f\f\f\f\f\f\f\f\f\f");
        try{

            System.out.print("Please enter the message you want to send: ");
            String contents = ProfNetwork.in.readLine();
            try{
                String query2 = String.format("SELECT COUNT(*) FROM MESSAGE");
                int count = esql.executeQuery(query2);
                String query = String.format("INSERT INTO message (msgId,senderId, receiverId, contents, deleteStatus, status) " + "VALUES('"+count+"','"+c_user+"', '"+receiverId+"', '"+contents+"', 0, 'Delivered')");
                esql.executeUpdate(query);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    public static void ListSentMessages(ProfNetwork esql, String c_user){
        try{
            String query = String.format("SELECT msgId, receiverId, status FROM message WHERE senderid = '%s' AND (deleteStatus = '0' OR deleteStatus = '2')", c_user);
            int result = esql.executeQueryAndPrintResult(query);
            if(result < 1){
                System.out.println("You have no sent messages.");
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
  /*  public static void ViewRecieveMessage(ProfNetwork esql, String c_user){
        try{
            String query = String.format("SELECT msgid, senderid, contents, status FROM message WHERE receiverid = '%s' AND (deleteStatus = '0' OR deleteStatus = '1')", c_user);
            int result = esql.executeQueryAndPrintResult(query);
            if(result < 1){
                System.out.println("You have no recieved messages.");
            }

        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

*/
    public static void ViewSentMessage(ProfNetwork esql, String c_user){
        try{
            System.out.print("Please enter the message id you want to view: ");
            String input = ProfNetwork.in.readLine();
            try{
                int msgId = Integer.parseInt(input.trim());
                try{
                    System.out.print("\nMessage: ");
                    String query = String.format("SELECT contents FROM message WHERE msgId = '%s' AND senderId = '%s'", msgId, c_user);
                    int result = esql.executeQueryAndPrintResult(query);
                    if(result < 1){
                        System.out.println("Message doesn't Exist");
                    }
                } catch (Exception e){
                    System.err.println(e.getMessage());
                }
            } catch (Exception e){
                System.out.println("Invalid.");
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    public static void Delete_Message(ProfNetwork esql, String c_user, String type){
        System.out.print("Please enter the message id that you would like to delete: ");
        try{
            String input = ProfNetwork.in.readLine();
            int msgId = Integer.parseInt(input.trim());
            try{
                if(type.equals("send")){
                    String query = String.format("SELECT deleteStatus FROM message WHERE msgId = '%s' AND senderId = '%s'", msgId, c_user);
                    List<List<String>> result = new ArrayList<List<String>>();
                    result = esql.executeQueryAndReturnResult(query);
                    try{
                        if(result.get(0).get(0).equals("0")){
                            query = String.format("UPDATE message set deleteStatus = '1' WHERE msgId = '%s'", msgId);
                            esql.executeUpdate(query);
                        } else{
                            query = String.format("DELETE FROM message WHERE msgId = '%s'", msgId);
                            esql.executeUpdate(query);
                        }
                    } catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                } else {
                    String query = String.format("SELECT deleteStatus FROM message WHERE msgId = '%s' AND receiverId = '%s'", msgId, c_user);
                    List<List<String>> result = new ArrayList<List<String>>();
                    result = esql.executeQueryAndReturnResult(query);
                    try{
                        if(result.get(0).get(0).equals("0")){

                            query = String.format("UPDATE message SET deleteStatus = '2' WHERE msgId = '%s'", msgId);
                            esql.executeUpdate(query);
                        } else{
                            query = String.format("DELETE FROM message WHERE msgId = '%s'", msgId);
                            esql.executeUpdate(query);
                        }
                    } catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                }
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        } catch (Exception e){
            System.out.println("Please enter a valid message id.");
        }
    }
}


