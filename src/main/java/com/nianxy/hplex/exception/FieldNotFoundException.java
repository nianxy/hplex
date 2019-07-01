package com.nianxy.hplex.exception;

public class FieldNotFoundException extends Exception {
    public FieldNotFoundException(String fieldName) {
        super("field object not found for [" + fieldName + "], note that the field name is case sensitive and should be annotated with @Column");
    }
}
