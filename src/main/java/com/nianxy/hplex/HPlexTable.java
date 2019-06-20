package com.nianxy.hplex;

import app.nianxy.commonlib.exceptionutils.ExceptionUtils;
import com.nianxy.hplex.cond.Cond;
import com.nianxy.hplex.cond.CondCompare;
import com.nianxy.hplex.exception.ExecutionFailedException;
import com.nianxy.hplex.exception.TableNotFoundException;
import com.nianxy.hplex.limit.Limit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class HPlexTable {
    private static final Logger logger = LogManager.getLogger(HPlexTable.class);

    private Class<?> clazz;
    private TableInfo tableInfo;
    private Connection connection;

    public HPlexTable(Class<?> clazz) throws TableNotFoundException {
        this(clazz, null);
    }

    public HPlexTable(Class<?> clazz, Connection connection) throws TableNotFoundException {
        this.clazz = clazz;
        this.connection = connection;
        tableInfo = HPlex.getTableInfo(this.clazz);
        if (tableInfo==null) {
            throw new TableNotFoundException(clazz.getName());
        }
    }

    protected Class<?> getClazz() {
        return clazz;
    }

    protected TableInfo getTableInfo() {
        return tableInfo;
    }

    /**
     * 返回一个对象的非null值属名性列表
     * @param obj
     * @return
     */
    public static List<String> getNotNullFields(Object obj) {
        List<String> fieldList = new ArrayList<>();
        Class cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            try {
                if (f.get(obj)!=null) {
                    fieldList.add(f.getName());
                }
            } catch (IllegalAccessException e) {
                logger.error("travel object attribute exception:" + ExceptionUtils.getTraceInfo(e));
            }
        }
        return fieldList;
    }

    /**
     * 返回一个此表的Query对象
     * @return
     */
    public Query query() {
        return new Query(this, connection);
    }

    /**
     * 一个helper函数，快速得到一个(field=value)的查询对象
     * @param field
     * @param value
     * @return
     */
    public Object fetchOne(String field, Object value) throws ExecutionFailedException {
        List list = query().addCond(Cond.compare(CondCompare.Compare.EQ, field, value))
                .setLimit(Limit.limit().setMaxSize(1)).execute();
        if (list.size()>0) {
            return list.get(0);
        }
        return null;
    }

    public Delete delete() {
        return new Delete(this, connection);
    }

    public Insert insert() {
        return new Insert(this, connection);
    }

    public Update update(Object data) {
        return new Update(this, data, connection);
    }
}
