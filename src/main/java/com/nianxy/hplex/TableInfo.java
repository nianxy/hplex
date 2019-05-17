package com.nianxy.hplex;

import com.nianxy.hplex.assign.ValueAssigner;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by nianxingyan on 17/8/16.
 */
class TableInfo implements FieldInfo {
    private Class<?> tableClass;
    private String tableName;
    private Map<String,ColumnInfo> columnsByName;
    private Map<String,ColumnInfo> columnsByMember;

    public TableInfo() {
        columnsByName = new TreeMap<>();
        columnsByMember = new TreeMap<>();
    }

    protected Class<?> getTableClass() {
        return tableClass;
    }

    protected String getTableName() {
        return tableName;
    }

    public void setTableClass(Class<?> tableClass) {
        this.tableClass = tableClass;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    protected Map<String, ColumnInfo> getColumnsByName() {
        return columnsByName;
    }

    protected Map<String, ColumnInfo> getColumnsByMember() {
        return columnsByMember;
    }

    @Override
    public String getColumnByField(String field) {
        return columnsByMember.get(field).getColumnName();
    }

    @Override
    public ValueAssigner getAssignerByField(String field) {
        return columnsByMember.get(field).getAssigner();
    }
}
