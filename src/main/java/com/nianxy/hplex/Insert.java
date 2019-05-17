package com.nianxy.hplex;

import app.nianxy.commonlib.exceptionutils.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by nianxingyan on 17/8/19.
 */
public class Insert {
    private static final Logger logger = LogManager.getLogger(Insert.class);

    private HPlexTable table;
    private TableInfo tableInfo;
    private List<Object> data;
    private List<ColumnInfo> columns;
    private List<ColumnInfo> update;
    private List<String> customUpdate;

    private boolean isSetAutoInc;
    private ColumnInfo autoIncField;

    protected Insert(HPlexTable table) {
        this.table = table;
        tableInfo = table.getTableInfo();
        isSetAutoInc = false;
        data = new ArrayList<>();
    }

    /**
     * 增加一条插入数据对象
     * @param data
     * @return
     */
    public Insert add(Object data) {
        this.data.add(data);
        return this;
    }

    /**
     * 插入时是否手动设置自增字段，默认不设置
     * @param set 如果要手动设置自增字段，则设置为true
     * @return
     */
    public Insert setAutoInc(boolean set) {
        this.isSetAutoInc = set;
        return this;
    }

    /**
     * 如果调用此函数，则在insert语句中，将自定义需要插入的字段，而不是所有字段
     * @param field 数据表对象的成员名称，注意不是列名
     * @return
     */
    public Insert addField(String field) {
        if (columns ==null) {
            this.columns = new ArrayList<>();
        }
        ColumnInfo ci = tableInfo.getColumnsByMember().get(field);
        if (ci==null) {
            throw new RuntimeException("column is not in the table object:" + field);
        }
        columns.add(ci);
        return this;
    }

    /**
     * 如果需要on duplicat key update子句，可以通过此函数设置要更新的字段
     * @param field 数据表对象的成员名称
     * @return
     */
    public Insert addUpdate(String field) {
        if (update==null) {
            this.update = new ArrayList<>();
        }
        ColumnInfo ci = tableInfo.getColumnsByMember().get(field);
        if (ci==null) {
            throw new RuntimeException("column is not in the table object:" + field);
        }
        update.add(ci);
        return this;
    }

    /**
     * 在on duplicate key update子句中，使用自定义的SQL，比如：
     * {@code addCustomUpdate("count=count+1");}
     * 将生成：{@code on duplicat key update count=count+1}
     * 此函数可以和addUpdate同时使用，此时将所有设置的字段拼接在一起
     * @param sql 自定义SQL
     * @return
     */
    public Insert addCustomUpdate(String sql) {
        if (customUpdate==null) {
            this.customUpdate = new ArrayList<>();
        }
        customUpdate.add(sql);
        return this;
    }

    /**
     * 执行插入操作，返回影响的数据条数
     * @return
     */
    public int execute() throws Exception {
        // 先检查是否有自增字段
        checkAutoIncField();

        Connection conn = HPlex.getConfigure().getDataSource().getConnection();
        if (conn==null) {
            throw new Exception("execute get connection failed");
        }
        try {
            PreparedStatement pstmt = setupPrepareStatement(conn, Statement.NO_GENERATED_KEYS);
            int count = pstmt.executeUpdate();
            return count;
        } finally {
            if (conn!=null) {
                try {
                    conn.close();
                } catch (Exception e){} finally {
                    conn = null;
                }
            }
        }
    }

    /**
     * 只返回第一条插入的数据对应的ID
     * @return
     * @throws Exception
     */
    public long executeForID() throws Exception {
        // 先检查是否有自增字段
        checkAutoIncField();

        Connection conn = HPlex.getConfigure().getDataSource().getConnection();
        if (conn==null) {
            throw new Exception("executeForID get connection failed");
        }
        try {
            PreparedStatement pstmt = setupPrepareStatement(conn, Statement.RETURN_GENERATED_KEYS);
            int count = pstmt.executeUpdate();
            long id = 0;
            if (count>0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getLong(1);
                }
            }
            return id;
        } finally {
            if (conn!=null) {
                try {
                    conn.close();
                } catch (Exception e){} finally {
                    conn = null;
                }
            }
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
    private String getColumnNames() {
        StringBuffer sb = new StringBuffer();
        Stream<ColumnInfo> cols = (columns!=null?columns.stream():
                tableInfo.getColumnsByName().values().stream());
        cols.forEach(c->{
            if (!c.isAutoIncrement()||this.isSetAutoInc) {
                sb.append("`").append(c.getColumnName()).append("`,");
            }
        });
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    private String getValuesStr() {
        StringBuffer sb = new StringBuffer();
        int fcount = columns!=null?columns.size():
                tableInfo.getColumnsByName().values().size();
        if (autoIncField!=null && !this.isSetAutoInc) {
            // 说明当前字段列表中存在自增字段
            --fcount;
        }
        for (int i=0;i<data.size();++i) {
            sb.append("(").append(joinSameStr("?",fcount,",")).append("),");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    private static String joinSameStr(String metastr, int n, String splitor) {
        StringBuffer sb = new StringBuffer();
        for (int i=0;i<n;++i) {
            sb.append(metastr).append(splitor);
        }
        if (n>0) {
            sb.delete(sb.length()-metastr.length(), sb.length());
        }
        return sb.toString();
    }

    private String getUpdateStr() {
        // 多行插入时，update子句可以写成这样：
        //   insert into table (A,B,C) values
        //     (1,2,3),(2,3,4),(3,4,5) on duplicate key upate
        //     B=values(B),C=values(C)
        StringBuffer sb = new StringBuffer();
        if (update!=null && update.size()>0) {
            sb.append(" on duplicate key update ");
            update.stream().forEach(c->{
                sb.append("`").append(c.getColumnName()).append("`=values(`").append(c.getColumnName()).append("`),");
            });
        }
        if (customUpdate!=null && customUpdate.size()>0) {
            if (sb.length()==0) {
                sb.append(" on duplicate key update ");
            }
            customUpdate.stream().forEach(s->{
                sb.append(s).append(",");
            });
        }
        if (sb.length()>0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    private void setPrepareStatementValues(PreparedStatement pstmt) throws SQLException {
        int idx = 0;
        Collection<ColumnInfo> cols = (columns!=null?columns:
                tableInfo.getColumnsByName().values());
        for (Object obj:data) {
            //idx = 0;
            Iterator<ColumnInfo> iter = cols.iterator();
            while (iter.hasNext()) {
                ColumnInfo ci = iter.next();
                if (ci.isAutoIncrement() && !this.isSetAutoInc) {
                    continue;
                }
                Field field = ci.getField();
                try {
                    ci.getAssigner().assign(pstmt, ++idx, field.get(obj));
                } catch (IllegalAccessException e) {
                    logger.error("get value for field " + field.getName() + " exception:" +
                            ExceptionUtils.getTraceInfo(e));
                    throw new RuntimeException("get value for field " + field.getName() + " failed!");
                }
            }
        }
    }

    private PreparedStatement setupPrepareStatement(Connection conn, int autoinc) throws SQLException {
        // 先拼SQL
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tableInfo.getTableName())
                .append(" (").append(getColumnNames()).append(")values")
                .append(getValuesStr())
                .append(getUpdateStr());

        logger.trace(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql.toString(), autoinc);

        // 设置参数
        setPrepareStatementValues(pstmt);

        return pstmt;
    }
}
