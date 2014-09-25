package org.dbpedia.extraction.live.mirror.sparul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/24/14 11:16 AM
 */
public class SPARULVosExecutor implements SPARULExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SPARULVosExecutor.class);

    public SPARULVosExecutor() {
    }

    @Override
    public void executeSPARUL(String sparulQuery) throws SPARULException {

        if (sparulQuery.startsWith("SPARQL ")) {
            execSQLWrapper(sparulQuery);
        } else {
            execSQLWrapper("SPARQL " + sparulQuery);
        }
    }


    /*
    * Execs an SQL query and returns true if everything went ok or false  in case of exception
    * */

    private void execSQLWrapper(String query) throws SPARULException {
        try {
            execSQL(query);
        } catch (Exception e) {

            String expMessage = e.toString();
            // This means that VOS is probably down
            if (expMessage.contains("Broken pipe") || expMessage.contains("Virtuoso Communications Link Failure")) {
                logger.error("Virtuoso is probably down, exiting...", e);
                System.exit(1);
            }
            //When Virtuoso commits a CHECKPOINT we fail to insert anything
            //and get a Transaction deadlock exception
            //here we lock everything and try X attempts every Y seconds
            if (expMessage.contains("Transaction deadlock")) {
                synchronized (SPARULVosExecutor.class) {
                    //The checkpoint lasts around 2-3 minutes
                    int attempts = 10;
                    int sleep = 30000;
                    for (int i = 1; i < attempts; i++) {
                        try {
                            logger.warn("Transaction Deadlock, retrying query: " + i + "/" + attempts);
                            execSQL(query);
                            //When no exception return
                            return;
                        } catch (Exception e1) {
                            logger.warn("Transaction Deadlock, retrying query: " + i + "/" + attempts + "(FAILED)");
                            try {
                                Thread.sleep(sleep);
                            } catch (InterruptedException e2) {
                                //do nothing
                            }
                        }
                    }
                }
            }
            throw new SPARULException(e);
        }
    }

    private static void execSQL(String query) throws SPARULException {

        Connection conn = null;
        Statement stmt = null;
        ResultSet result = null;
        try {
            conn = JDBCPoolConnection.getPoolConnection();
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (Exception e) {
            throw new SPARULException(e);


        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception e) {
                logger.warn("Cannot close ResultSet", e);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                logger.warn("Cannot close Statement", e);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                logger.warn("Cannot close Connection", e);
            }
        }
    }
}
