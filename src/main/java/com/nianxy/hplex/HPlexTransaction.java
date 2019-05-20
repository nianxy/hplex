package com.nianxy.hplex;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

public class HPlexTransaction {
    private static final Logger logger = LogManager.getLogger(HPlexTransaction.class);

    private Connection connection;
    private boolean started;
    private boolean oriAutoCommit;

    public HPlexTransaction() {
        started = false;
    }

    public void start() throws Exception {
        Connection conn = HPlex.getConfigure().getDataSource().getConnection();
        if (conn==null) {
            throw new Exception("get connection failed!");
        }
        connection = conn;
        oriAutoCommit = conn.getAutoCommit();
        if (oriAutoCommit) {
            conn.setAutoCommit(false);
        }
        started = true;
    }

    public HPlexTable hPlexTable(Class<?> clazz) throws Exception {
        if (!started) {
            throw new Exception("transaction is not started");
        }
        return new HPlexTable(clazz, connection);
    }

    public void commit() throws Exception {
        if (!started) {
            throw new Exception("transaction is not started");
        }
        connection.commit();
        connection.close();
        if (!oriAutoCommit) {
            connection.setAutoCommit(oriAutoCommit);
        }
    }

    public void rollback() throws Exception {
        if (!started) {
            throw new Exception("transaction is not started");
        }
        connection.rollback();
        connection.close();
        if (!oriAutoCommit) {
            connection.setAutoCommit(oriAutoCommit);
        }
    }

    public boolean isStarted() {
        return started;
    }
}
