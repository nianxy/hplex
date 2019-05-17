package com.nianxy.hplex;

import com.nianxy.hplex.assign.ValueAssigner;

import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * Created by nianxingyan on 17/8/16.
 */
class ColumnInfo {
    private String columnName;
    private boolean isAutoIncrement;
    private boolean isAutoGenerate;
    private Field field;
    private ValueAssigner assigner;

    public void assign(Object obj, ResultSet rs) throws Exception {
        assigner.assign(obj, field, rs, columnName);
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public ValueAssigner getAssigner() {
        return assigner;
    }

    public void setAssigner(ValueAssigner assigner) {
        this.assigner = assigner;
    }

    public boolean isAutoGenerate() {
        return isAutoGenerate;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        isAutoGenerate = autoGenerate;
    }
}
