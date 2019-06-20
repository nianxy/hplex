package com.nianxy.hplex.exception;

public class TableNotFoundException extends Exception {
    public TableNotFoundException(String className) {
        super(String.format("Class [%s] was not mapped, please invoke " +
                "HPlexConfigure.registTable() to regist the class", className));
    }
}
