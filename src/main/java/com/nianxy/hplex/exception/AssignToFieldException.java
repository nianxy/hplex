package com.nianxy.hplex.exception;

import java.lang.reflect.Field;

public class AssignToFieldException extends Exception {
    public AssignToFieldException(Field field, Throwable cause) {
        super(String.format("assign value to field [%s] failed!", field.getType().getName()), cause);
    }
}
