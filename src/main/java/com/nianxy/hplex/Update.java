package com.nianxy.hplex;

import app.nianxy.commonlib.exceptionutils.ExceptionUtils;
import com.nianxy.hplex.cond.Cond;
import com.nianxy.hplex.cond.ICond;
import com.nianxy.hplex.limit.ILimit;
import com.nianxy.hplex.limit.Limit;
import com.nianxy.hplex.order.IOrder;
import com.nianxy.hplex.order.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by nianxingyan on 17/8/19.
 */
public class Update {
    private static final Logger logger = LogManager.getLogger(Update.class);

    private HPlexTable table;
    private Connection connection;
    private TableInfo tableInfo;
    private List<ColumnInfo> columns;
    private List<ICond> conds;
    private List<IOrder> orders;
    private ILimit limit;
    private Object data;

    private boolean isSetAutoInc;
    private ColumnInfo autoIncField;

    protected Update(HPlexTable table, Object datam, Connection conn) {
        this.table = table;
        this.data = data;
        connection = conn;
        if (!table.getClazz().isInstance(data)) {
            throw new RuntimeException("object is not the instance of " + table.getClazz().getName());
        }
        tableInfo = table.getTableInfo();
        isSetAutoInc = false;
        conds = new ArrayList<>();
    }

    /**
     * 设置数据条件
     * @param cond
     * @return
     */
    public Update addCond(ICond cond) {
        this.conds.add(cond);
        return this;
    }

    /**
     * 更新操作是否更新自增字段，默认不更新
     * @param set 如果需要更新自增字段，则设置为true
     * @return
     */
    public Update setAutoInc(boolean set) {
        this.isSetAutoInc = set;
        return this;
    }

    /**
     * 如果调用此函数，则在insert语句中，将自定义需要插入的字段，而不是所有字段
     * @param field 数据表对象的成员名称，注意不是列名
     * @return
     */
    public Update addField(String field) {
        if (columns==null) {
            columns = new ArrayList<>();
        }
        columns.add(tableInfo.getColumnsByMember().get(field));
        return this;
    }

    /**
     * 设置更新顺序
     * @param order
     * @return
     */
    public Update addOrder(IOrder order) {
        if (orders==null) {
            orders = new ArrayList<>();
        }
        orders.add(order);
        return this;
    }

    /**
     * 设置更新数据数量限制
     * @param limit
     * @return
     */
    public Update setLimit(ILimit limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 执行插入操作，返回影响的数据条数
     * @return
     */
    public int execute() throws Exception {
        // 先检查是否有自增字段
        checkAutoIncField();

        HPConnection conn = new HPConnection(connection);
        try {
            PreparedStatement pstmt = setupPrepareStatement(conn.getConnection());
            int count = pstmt.executeUpdate();
            return count;
        } catch (Exception e) {
            throw e;
        } finally {
            conn.close();
        }
    }

    private void checkAutoIncField() {
        Collection<ColumnInfo> cols = (columns!=null?columns:
                tableInfo.getColumnsByName().values());
        Iterator<ColumnInfo> iter = cols.iterator();
        while (iter.hasNext()) {
            ColumnInfo ci = iter.next();
            if (ci.isAutoIncrement()) {
                this.autoIncField = ci;
                break;
            }
        }
    }

    /**
     * 返回逗号分隔的自定义字段列表
     * @return
     */
    private String getColumnStatement() {
        StringBuffer sb = new StringBuffer();
        Stream<ColumnInfo> cols = (columns!=null?columns.stream():
                tableInfo.getColumnsByName().values().stream());
        cols.forEach(c->{
            if (!c.isAutoIncrement()||this.isSetAutoInc)
                sb.append(c.getColumnName()).append("=?,");
        });
        if (sb.length()>0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private int setPrepareStatementColumnValues(PreparedStatement pstmt, int paramIndex) throws SQLException {
        int idx = paramIndex;
        Collection<ColumnInfo> cols = (columns!=null?columns:
                tableInfo.getColumnsByName().values());
        Iterator<ColumnInfo> iter = cols.iterator();
        while (iter.hasNext()) {
            ColumnInfo ci = iter.next();
            if (ci.isAutoIncrement() && !this.isSetAutoInc)
                continue;
            Field field = ci.getField();
            try {
                ci.getAssigner().assign(pstmt, idx++, field.get(data));
            } catch (IllegalAccessException e) {
                logger.error("get value for field " + field.getName() + " exception:" +
                        ExceptionUtils.getTraceInfo(e));
                throw new RuntimeException("get value for field " + field.getName() + " failed!");
            }
        }
        return idx;
    }

    private PreparedStatement setupPrepareStatement(Connection conn) throws SQLException {
        // 先拼SQL
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(tableInfo.getTableName())
                .append(" set ").append(getColumnStatement())
                .append(Cond.getWhereClause(conds, tableInfo))
                .append(Order.getOrderClause(orders, tableInfo))
                .append(Limit.getLimitClause(limit));

        logger.trace(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql.toString());

        // 设置参数
        int paramIndex = 1;
        paramIndex = setPrepareStatementColumnValues(pstmt, paramIndex);
        paramIndex = Cond.setWherePrepareStatement(conds, pstmt, paramIndex, tableInfo);

        return pstmt;
    }
}
