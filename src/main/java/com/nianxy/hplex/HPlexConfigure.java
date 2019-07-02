package com.nianxy.hplex;

import com.nianxy.hplex.annotation.Column;
import com.nianxy.hplex.annotation.Table;
import com.nianxy.hplex.assign.*;
import com.nianxy.hplex.exception.InvalidAutoincFieldTypeException;
import com.nianxy.hplex.exception.NoJSONConvertException;
import com.nianxy.hplex.exception.UnsupportedFieldTypeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class HPlexConfigure {
    private static final Logger logger = LogManager.getLogger(HPlexConfigure.class);

    private Map<String, TableInfo> classColumns = new HashMap<>();
    private SimpleDataSoruce ds;
    private IJSONConvert jsonConvert;

    private static ValueAssigner getAssigner(Field field) {
        Class fieldClass = field.getType();
        if (int.class.isAssignableFrom(fieldClass) || Integer.class.isAssignableFrom(fieldClass)) {
            return new IntegerAssigner();
        } else if (long.class.isAssignableFrom(fieldClass) || Long.class.isAssignableFrom(fieldClass)) {
            return new LongAssigner();
        } else if (double.class.isAssignableFrom(fieldClass) || Double.class.isAssignableFrom(fieldClass)) {
            return new DoubleAssigner();
        } else if (float.class.isAssignableFrom(fieldClass) || Float.class.isAssignableFrom(fieldClass)) {
            return new FloatAssigner();
        } else if (byte.class.isAssignableFrom(fieldClass) || Byte.class.isAssignableFrom(fieldClass)) {
            return new ByteAssigner();
        } else if (boolean.class.isAssignableFrom(fieldClass) || Boolean.class.isAssignableFrom(fieldClass)) {
            return new BooleanAssigner();
        } else if (String.class.isAssignableFrom(fieldClass)) {
            return new StringAssigner();
        } else if (Date.class.isAssignableFrom(fieldClass)) {
            return new DateAssigner();
        } else if (IJSONColumn.class.isAssignableFrom(fieldClass)) {
            JSONAssigner assigner =  new JSONAssigner();
            assigner.setObjectType(fieldClass);
            return assigner;
        }
        return null;
    }

    public HPlexConfigure setJSONConvert(IJSONConvert convert) {
        this.jsonConvert = convert;
        return this;
    }

    public IJSONConvert getJsonConvert() {
        return jsonConvert;
    }

    public HPlexConfigure registTable(Class<?> c) throws UnsupportedFieldTypeException, InvalidAutoincFieldTypeException {
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
                        throw new InvalidAutoincFieldTypeException(tableInfo.getTableName(),
                                columnInfo.getColumnName());
                    }
                }
                columnInfo.setField(f);
                columnInfo.setAssigner(getAssigner(f));
                if (columnInfo.getAssigner()==null) {
                    throw new UnsupportedFieldTypeException(tableInfo.getTableName(),
                            columnInfo.getColumnName(), f.getType().getName());
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
