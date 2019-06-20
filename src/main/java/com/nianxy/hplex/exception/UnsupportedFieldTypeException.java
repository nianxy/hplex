package com.nianxy.hplex.exception;

public class UnsupportedFieldTypeException extends Exception {
    private String table;
    private String field;
    private String type;

    public void setTable(String table) {
        this.table = table;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UnsupportedFieldTypeException(String table, String field, String type) {
        this.table = table;
        this.field = field;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return String.format("unsupported field type [%s] of field [%s.%s]", type, table, field);
    }
}
