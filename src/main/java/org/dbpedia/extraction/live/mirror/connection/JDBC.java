package org.dbpedia.extraction.live.mirror.connection;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.dbpedia.extraction.live.mirror.helper.Global;

import java.sql.*;


/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: May 29, 2011
 * Time: 1:01:10 PM
 * This class provides support for reading JDBC information, this information is required for live extraction
 */
public class JDBC{

    private static final int JDBC_MAX_LONGREAD_LENGTH = 8000;
    //Initializing the Logger
    private static Logger logger = Logger.getLogger(JDBC.class);

    String dsn;
    String user;
    String pw;
    static Connection con = null;
    String previous;
    final int wait = 5;
    final int cutstring = 1000;

    static {
        logger = Logger.getLogger(JDBC.class);
        logger.addAppender(new ConsoleAppender(new SimpleLayout()));
    }

    public JDBC(String DSN, String USER, String Password)
    {
        this.dsn = DSN;
        this.user = USER;
        this.pw = Password;

        if(con == null)
            this.connect();

    }

    public static JDBC getDefaultConnection()
    {
        String dataSourceName = Global.options.get("Store.dsn");
        String username = Global.options.get("Store.user");
        String password = Global.options.get("Store.pw");

        return new JDBC(dataSourceName, username, password);
    }

    /*
    * Blocks until connection exists
    *
    * */
    public void connect(boolean debug)
    {
        try{
            //Make sure that the JDBC driver for virtuoso exists
            Class.forName("virtuoso.jdbc4.Driver");


            boolean FailedOnce = false;

            Connection Conn = DriverManager.getConnection(this.dsn, this.user, this.pw);

            while(Conn == null){
                logger.warn("JDBC connection to " + this.dsn + " failed, waiting for "
                                                    + wait + " and retrying");
                Thread.sleep(wait);

                //Retrying to connect to the database
                Conn = DriverManager.getConnection(this.dsn, this.user, this.pw);
            }
            if(debug)
            {
                logger.info("JDBC connection re-established");
            }
            con = Conn;
        }
        catch(ClassNotFoundException exp)
        {
           logger.fatal("JDBC driver of Virtuoso cannot be loaded", exp);
           System.exit(1);
        }
        catch(Exception exp)
        {
             logger.warn(exp.getMessage() + " Function connect ", exp);
        }
    }

    public void connect(){
        connect(false);
    }

     /*
	 * returns the jdbc statement
	 * */

	public PreparedStatement prepare(String query)
    {
        try{

    	 	PreparedStatement result = con.prepareStatement(query);

            return result;
        }
        catch(Exception exp){

            return null;
        }

	}

    public boolean executeStatement(PreparedStatement sqlStatement, String[] parameterList)
    {
        //Assert.assertNotNull("Statement cannot be null", sqlStatement);
        boolean successfulExecution = false;
        try{
            if((con == null) || (con.isClosed()))
                con = DriverManager.getConnection(this.dsn, this.user, this.pw);

            for(int i=0;i<parameterList.length; i++)
            {
                sqlStatement.setString(i+1, parameterList[i]);
            }
            successfulExecution = sqlStatement.execute();
//            successfulExecution = true;
            sqlStatement.close();
            //con.close();
        }
        catch(Exception exp){
            logger.warn(exp.getMessage() + " Function executeStatement ", exp);
            successfulExecution = false;
        }

        return successfulExecution;
    }


    //This function executes the passed query
    public ResultSet exec(String query)
    {
        //Assert.assertTrue("Query cannot be null or empty", (query != null && query != ""));
        ResultSet result = null;
        try
        {
            if((con == null) || (con.isClosed()))
                con = DriverManager.getConnection(this.dsn, this.user, this.pw);

            Statement requiredStatement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //query = "DELETE FROM dbpedia_trIples WHERE OAIID = 7284";
            result = requiredStatement.executeQuery(query);
            logger.info("::SUCCESS ( "+ result +" ): ");

        }
        catch(Exception exp)
        {

            logger.warn(exp.getMessage() + " Function executeStatement ", exp);
        }

        return result;
	}

}
