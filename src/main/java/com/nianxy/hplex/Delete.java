package com.nianxy.hplex;

import com.nianxy.hplex.cond.Cond;
import com.nianxy.hplex.cond.ICond;
import com.nianxy.hplex.limit.ILimit;
import com.nianxy.hplex.limit.Limit;
import com.nianxy.hplex.order.IOrder;
import com.nianxy.hplex.order.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class Delete {
    private static final Logger logger = LogManager.getLogger(Delete.class);

    private Connection connection;
    private HPlexTable table;
    private TableInfo tableInfo;
    private List<ICond> conds;
    private List<IOrder> orders;
    private ILimit limit;

    protected Delete(HPlexTable table, Connection conn) {
        this.table = table;
        connection = conn;
        tableInfo = table.getTableInfo();
        conds = new ArrayList<>();
    }

    /**
     * 设置删除条件
     * @param cond
     * @return
     */
    public Delete addCond(ICond cond) {
        conds.add(cond);
        return this;
    }

    /**
     * 设置删除的数据顺序
     * @param order
     * @return
     */
    public Delete addOrder(IOrder order) {
        if (orders==null) {
            orders = new ArrayList<>();
        }
        orders.add(order);
        return this;
    }

    /**
     * 设置删除数量限制
     * @param limit
     * @return
     */
    public Delete setLimit(ILimit limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 干掉你们
     * @return
     * @throws Exception
     */
    public int execute() throws Exception {
        HPConnection conn = new HPConnection(connection);
        int n = 0;
        try {
            PreparedStatement pstmt = setupPrepareStatement(conn.getConnection());
            n = pstmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            conn.close();
        }
        return n;
    }

    private PreparedStatement setupPrepareStatement(Connection conn) throws SQLException {
        // 先拼SQL
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(tableInfo.getTableName())
                .append(Cond.getWhereClause(conds, tableInfo))
                .append(Order.getOrderClause(orders, tableInfo))
                .append(Limit.getLimitClause(limit));

        logger.trace(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql.toString());

        // 设置参数
        int paramIndex = 1;
        Cond.setWherePrepareStatement(conds, pstmt, paramIndex, tableInfo);

        return pstmt;
    }
}
