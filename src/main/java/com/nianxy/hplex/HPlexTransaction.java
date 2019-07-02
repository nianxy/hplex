package com.nianxy.hplex;


import com.nianxy.hplex.exception.NoConnectionException;
import com.nianxy.hplex.exception.TableNotFoundException;
import com.nianxy.hplex.exception.TransactionNotStartedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class HPlexTransaction {
    private static final Logger logger = LogManager.getLogger(HPlexTransaction.class);

    private Connection connection;
    private boolean started;
    private boolean finished;
    private boolean inTransaction;
    private boolean oriAutoCommit;

    public HPlexTransaction() {
        started = false;
        finished = false;
    }

    public void start() throws NoConnectionException, SQLException {
        Connection conn = HPlex.getConfigure().getDataSource().getConnection();
        if (conn==null) {
            throw new NoConnectionException();
        }
        connection = conn;
        oriAutoCommit = conn.getAutoCommit();
        if (oriAutoCommit) {
            conn.setAutoCommit(false);
        }
        started = true;
        inTransaction = true;
    }

    /**
     * 调用此方法前需要Transaction已经启动
     * @param clazz
     * @return
     */
    public HPlexTable hPlexTable(Class<?> clazz) throws TransactionNotStartedException, TableNotFoundException {
        if (!started) {
            throw new TransactionNotStartedException();
        }
        return new HPlexTable(clazz, connection);
    }

    public void commit() throws TransactionNotStartedException, SQLException {
        if (!started) {
            throw new TransactionNotStartedException();
        }
        connection.commit();
        connection.close();
        if (!oriAutoCommit) {
            connection.setAutoCommit(oriAutoCommit);
        }
        finished = true;
        inTransaction = false;
    }

    public void rollback() throws TransactionNotStartedException, SQLException {
        if (!started) {
            throw new TransactionNotStartedException();
        }
        connection.rollback();
        connection.close();
        if (!oriAutoCommit) {
            connection.setAutoCommit(oriAutoCommit);
        }
        finished = true;
        inTransaction = false;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isInTransaction() {
        return inTransaction;
    }
}
