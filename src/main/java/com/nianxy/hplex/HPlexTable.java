package com.nianxy.hplex;

import com.nianxy.hplex.cond.Cond;
import com.nianxy.hplex.cond.CondCompare;
import com.nianxy.hplex.limit.Limit;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class HPlexTable {
    private static final Logger logger = Logger.getLogger(HPlexTable.class);

    private Class<?> clazz;
    private TableInfo tableInfo;
    private Query query;

    public HPlexTable(Class<?> clazz) throws Exception {
        this.clazz = clazz;
        tableInfo = HPlex.getTableInfo(this.clazz);
        if (tableInfo==null) {
            throw new Exception("class " + clazz.getName() + " is not mapped, please " +
                    "call HPlexConfigure.registTable() to regist the class");
        }
        query = new Query(this);
    }

    protected Class<?> getClazz() {
        return clazz;
    }

    protected TableInfo getTableInfo() {
        return tableInfo;
    }

    /**
     * 返回一个此表的Query对象
     * @return
     */
    public Query query() {
        return new Query(this);
    }

    /**
     * 一个helper函数，快速得到一个(field=value)的查询对象
     * @param field
     * @param value
     * @return
     */
    public Object fetchOne(String field, Object value) throws Exception {
        List list = query().addCond(Cond.compare(CondCompare.Compare.EQ, field, value))
                .setLimit(Limit.limit().setMaxSize(1)).execute();
        if (list.size()>0) {
            return list.get(0);
        }
        return null;
    }

    public Delete delete() {
        return new Delete(this);
    }

    public Insert insert() {
        return new Insert(this);
    }

    public Update update(Object data) {
        return new Update(this, data);
    }
}
