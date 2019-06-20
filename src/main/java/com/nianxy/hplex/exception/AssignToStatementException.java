package com.nianxy.hplex.exception;

public class AssignToStatementException extends Exception {
    public AssignToStatementException(Throwable cause) {
        super("assign value to statement failed!", cause);
    }
}
