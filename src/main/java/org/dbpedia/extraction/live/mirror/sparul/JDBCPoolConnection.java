package org.dbpedia.extraction.live.mirror.sparul;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.apache.log4j.Logger;
import org.dbpedia.extraction.live.mirror.helper.Global;

import java.sql.Connection;
import java.sql.SQLException;

public final class JDBCPoolConnection {
    //Initializing the Logger
    private static Logger logger = Logger.getLogger(JDBCPoolConnection.class);

    private static volatile BoneCP connectionPool = null;

    protected JDBCPoolConnection() {
    }

    private static void initConnection() {

        try {
            BoneCPConfig config = new BoneCPConfig();
            Class.forName(Global.options.get("Store.class"));
            config.setJdbcUrl(Global.options.get("Store.dsn"));
            config.setUsername(Global.options.get("Store.user"));
            config.setPassword(Global.options.get("Store.pw"));
            connectionPool = new BoneCP(config); // setup the connection pool
        } catch (Exception e) {
            logger.fatal("Could not initialize Triple-Store connection! Exiting...");
            System.exit(1);
        }
    }


    public static Connection getPoolConnection() throws SQLException {
        if (connectionPool == null) {
            synchronized (JDBCPoolConnection.class) {
                if (connectionPool == null) {
                    initConnection();
                }
            }
        }
        return connectionPool.getConnection();
    }


    public static void shutdown() {
        if (connectionPool != null) {
            try {
                connectionPool.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
