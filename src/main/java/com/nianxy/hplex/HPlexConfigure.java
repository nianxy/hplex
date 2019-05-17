package com.nianxy.hplex;

import com.nianxy.hplex.annotation.Column;
import com.nianxy.hplex.annotation.Table;
import com.nianxy.hplex.assign.*;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class HPlexConfigure {
    private static final Logger logger = Logger.getLogger(HPlexConfigure.class);

    private Map<String, TableInfo> classColumns = new HashMap<>();
    private SimpleDataSoruce ds;

    private ValueAssigner getAssigner(String type) {
        if (type.equals("int")) {
            return new IntegerAssigner();
        } else if (type.equals("long")) {
            return new LongAssigner();
        } else if (type.equals("double")) {
            return new DoubleAssigner();
        } else if (type.equals("float")) {
            return new FloatAssigner();
        } else if (type.equals("byte")) {
            return new ByteAssigner();
        } else if (type.equals("java.lang.Integer")) {
            return new IntegerAssigner();
        } else if (type.equals("java.lang.Long")) {
            return new LongAssigner();
        } else if (type.equals("java.lang.Double")) {
            return new DoubleAssigner();
        } else if (type.equals("java.lang.Float")) {
            return new FloatAssigner();
        } else if (type.equals("java.lang.String")) {
            return new StringAssigner();
        } else if (type.equals("java.lang.Byte")) {
            return new ByteAssigner();
        } else if (type.equals("java.util.Date")) {
            return new DateAssigner();
        }
        return null;
    }

    public HPlexConfigure registTable(Class<?> c) throws Exception {
        if (!c.isAnnotationPresent(Table.class)) {
            new Exception("class " + c.getName() + " is not annotated with Table");
        }

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableClass(c);
        tableInfo.setTableName(c.getAnnotation(Table.class).value());

        Field fields[] = c.getDeclaredFields();
        for (Field f:fields) {
            if (f.isAnnotationPresent(Column.class)) {
                ColumnInfo columnInfo = new ColumnInfo();
                Column col = f.getDeclaredAnnotation(Column.class);
                columnInfo.setColumnName(col.value());
                columnInfo.setAutoIncrement(col.autoinc());
                if (col.autoinc()) {
                    // 自增字段必须是整数
                    String type = f.getType().getName();
                    if (!type.equals("int") && !type.equals("long") &&
                            !type.equals("java.lang.Integer") &&
                            !type.equals("java.lang.Long")) {
                        throw new Exception("auto-increment columnt must be an integer");
                    }
                }
                columnInfo.setField(f);
                columnInfo.setAssigner(getAssigner(f.getType().getName()));
                if (columnInfo.getAssigner()==null) {
                    throw new Exception("unsupported fileld type:" + tableInfo.getTableClass().getName() +
                            ":" + f.getName() + ":" + f.getType().getName());
                }
                if (columnInfo.getColumnName().isEmpty()) {
                    columnInfo.setColumnName(f.getName());
                }
                f.setAccessible(true);
                tableInfo.getColumnsByName().put(columnInfo.getColumnName(), columnInfo);
                tableInfo.getColumnsByMember().put(f.getName(), columnInfo);
            }
        }

        classColumns.put(c.getName(), tableInfo);
        logger.trace("add Table class " + c.getName());

        return this;
    }

    protected TableInfo getTableInfo(Class<?> c) {
        return classColumns.get(c.getName());
    }

    protected SimpleDataSoruce getDataSource() {
        return ds;
    }

    public HPlexConfigure setDataSource(SimpleDataSoruce ds) {
        this.ds = ds;
        return this;
    }
}
