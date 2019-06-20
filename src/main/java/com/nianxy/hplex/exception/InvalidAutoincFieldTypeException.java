package com.nianxy.hplex.exception;

public class InvalidAutoincFieldTypeException extends Exception {
    public InvalidAutoincFieldTypeException(String table, String field) {
        super(String.format("auto-increment columnt must be a number type [%s.%s]", table, field));
    }
}
