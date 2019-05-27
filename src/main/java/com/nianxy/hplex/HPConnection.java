package com.nianxy.hplex;

import app.nianxy.commonlib.exceptionutils.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

class HPConnection {
    private static Logger logger = LogManager.getLogger(HPConnection.class);

    private Connection connection;
    private boolean realClose;

    public HPConnection(Connection connection) throws Exception {
        realClose = connection==null;
        if (realClose) {
            connection = HPlex.getConfigure().getDataSource().getConnection();
            if (connection==null) {
                throw new Exception("get connection failed!");
            }
        }
        this.connection = connection;
    }

    public void close() {
        if (realClose) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Close connection exception:" + ExceptionUtils.getTraceInfo(e));
            }
            connection = null;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}