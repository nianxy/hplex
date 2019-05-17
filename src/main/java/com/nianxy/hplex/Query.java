package com.nianxy.hplex;

import com.nianxy.hplex.cond.Cond;
import com.nianxy.hplex.cond.ICond;
import com.nianxy.hplex.limit.ILimit;
import com.nianxy.hplex.limit.Limit;
import com.nianxy.hplex.order.IOrder;
import com.nianxy.hplex.order.Order;
import com.nianxy.hplex.aggregation.Aggregate;
import com.nianxy.hplex.aggregation.AggregateGroup;
import com.nianxy.hplex.aggregation.IAggregate;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class Query {
    private static final Logger logger = Logger.getLogger(Query.class);

    private HPlexTable table;
    private TableInfo tableInfo;
    private List<ICond> conds;
    private List<ColumnInfo> columns;
    private List<IAggregate> aggregateFuncs;
    private IAggregate aggregateGroup;
    private List<IOrder> orders;
    private ILimit limit;

    protected Query(HPlexTable table) {
        this.table = table;
        tableInfo = table.getTableInfo();
        conds = new ArrayList<>();
    }

    /**
     * 自定义返回字段列表，如果不设置的话，会返回所有字段，一旦调用此函数，则只返回指定字段
     * @param field 对象的字段名称，注意不是数据库表的列名
     * @return
     */
    public Query addField(String field) {
        if (columns==null) {
            columns = new ArrayList<>();
        }
        columns.add(tableInfo.getColumnsByMember().get(field));
        return this;
    }

    /**
     * 返回自定义的指定列对应的字段列表
     * @return 如果有自定义列名的话，则返回相应字段列表，否则返回null
     */
    protected List<ColumnInfo> getColumns() {
        return columns;
    }

    /**
     * 返回逗号分隔的自定义字段列表，如果没有自定义，则返回“*”
     * @return
     */
    private String getColumnNames() {
        if (columns!=null) {
            StringBuffer sb = new StringBuffer();
            columns.stream().forEach(f->{
                sb.append(f.getColumnName()).append(",");
            });
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        } else {
            return "*";
        }
    }

    /**
     * 增加一个条件。
     * 目前所有条件之间都是 and 关系
     * @param cond
     * @return
     */
    public Query addCond(ICond cond) {
        conds.add(cond);
        return this;
    }

    /**
     * 设置排序规则
     * @param order {@see IOrder}对象，可以通过{@See Order}类生成
     * @return
     */
    public Query addOrder(IOrder order) {
        if (orders==null) {
            orders = new ArrayList<>();
        }
        orders.add(order);
        return this;
    }

    /**
     * 设置数据返回数量
     * @param limit {@see ILimit}对象，可以通过{@see Limit}类生成
     * @return
     */
    public Query setLimit(ILimit limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 设置组合参数
     * @param func {@see IAggregate}对象，通过{@see Aggregate}类生成
     * @return
     */
    public Query addAggregateFunc(IAggregate func) {
        if (!func.isFunction()) {
            throw new RuntimeException("addAggregateFunc param is not a aggregate function!");
        }
        if (aggregateFuncs==null) {
            aggregateFuncs = new ArrayList<>();
        }
        aggregateFuncs.add(func);
        return this;
    }

    /**
     * 设置分组条件
     * @param groupby {@see AggregateGroup}对象
     * @return
     */
    public Query setGroupBy(AggregateGroup groupby) {
        this.aggregateGroup = groupby;
        return this;
    }

    /**
     * 执行count()操作，返回count结果
     * @return
     * @throws Exception
     */
    public long count() throws Exception {
        Connection conn = HPlex.getConfigure().getDataSource().getConnection();
        if (conn==null) {
            throw new Exception("execute get connection failed");
        }

        long count = 0;
        try {
            PreparedStatement pstmt = setupCountPrepareStatement(conn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn!=null) {
                try {
                    conn.close();
                } catch (Exception e){} finally {
                    conn = null;
                }
            }
        }
        return count;
    }

    /**
     * 返回单个数据对象
     * @return
     * @throws Exception
     */
    public Object fetchOne() throws Exception {
        List result = execute();
        if (result!=null && result.size()>0) {
            return result.get(0);
        }
        return null;
    }

    /**
     * 如果查询中存在AggregateFunc，则返回一个Object列表，每个对象对应于一个Func的结果，暂时只支持返回一条AggregateFunc数据。
     * @return
     * @throws Exception
     */
    public List execute() throws Exception {
        List list = new ArrayList<>();

        Connection conn = HPlex.getConfigure().getDataSource().getConnection();
        if (conn==null) {
            throw new Exception("execute get connection failed");
        }

        Collection<ColumnInfo> columnList = getColumns();
        if (columnList==null) {
            columnList = tableInfo.getColumnsByName().values();
        }
        try {
            PreparedStatement pstmt = setupPrepareStatement(conn);
            ResultSet rs = pstmt.executeQuery();
            if (aggregateFuncs==null) {
                while (rs.next()) {
                    list.add(assignRS2Object(columnList, rs));
                }
            } else {
                if (rs.next()) {
                    int count = rs.getMetaData().getColumnCount();
                    for (int i=1; i<=count; ++i) {
                        list.add(rs.getObject(i));
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn!=null) {
                try {
                    conn.close();
                } catch (Exception e){} finally {
                    conn = null;
                }
            }
        }

        return list;
    }

    private Object assignRS2Object(Collection<ColumnInfo> columnList, ResultSet rs) throws Exception {
        Object obj = table.getClazz().newInstance();
        for (ColumnInfo column : columnList) {
            column.assign(obj, rs);
        }
        return obj;
    }

    private PreparedStatement setupCountPrepareStatement(Connection conn) throws SQLException {
        // 先拼SQL
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from ").append(tableInfo.getTableName())
                .append(Cond.getWhereClause(conds, tableInfo));

        logger.trace(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql.toString());

        // 设置参数
        Cond.setWherePrepareStatement(conds, pstmt, 1, tableInfo);

        return pstmt;
    }

    private PreparedStatement setupPrepareStatement(Connection conn) throws SQLException {
        // 先拼SQL
        StringBuilder sql = new StringBuilder();

        sql.append("select ");
        if (aggregateFuncs==null) {
            sql.append(getColumnNames());
        } else {
            sql.append(Aggregate.getAggregateFuncString(aggregateFuncs, tableInfo));
        }
        sql.append(" from ").append(tableInfo.getTableName())
                .append(Cond.getWhereClause(conds, tableInfo))
                .append(Order.getOrderClause(orders, tableInfo));
        if (aggregateGroup!=null) {
            sql.append(aggregateGroup.getSQLStatement(tableInfo));
        }
        sql.append(Limit.getLimitClause(limit));

        logger.trace(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql.toString());

        // 设置参数
        int paramIndex = 1;
        Cond.setWherePrepareStatement(conds, pstmt, paramIndex, tableInfo);

        return pstmt;
    }
}
