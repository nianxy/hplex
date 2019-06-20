package com.nianxy.hplex.exception;

public class CreateTableInstanceFailedException extends Exception {
    public CreateTableInstanceFailedException(String tableName, Exception e) {
        super(String.format("create table instance failed:" + tableName), e);
    }
}
