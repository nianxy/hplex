package com.nianxy.hplex.assign;

import com.nianxy.hplex.exception.AssignToFieldException;
import com.nianxy.hplex.exception.AssignToStatementException;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class BooleanAssigner implements ValueAssigner {
    @Override
    public void assign(Object obj, Field field, ResultSet rs, String label) throws AssignToFieldException {
        try {
            Object v = rs.getObject(label);
            field.set(obj, v == null ? null : rs.getByte(label) == 1);
        } catch (Throwable e) {
            throw new AssignToFieldException(field, e);
        }
    }

    @Override
    public void assign(PreparedStatement pstmt, int idx, Object value) throws AssignToStatementException {
        try {
            if (value == null) {
                pstmt.setNull(idx, Types.TINYINT);
            } else {
                pstmt.setByte(idx, ((Boolean) value) ? (byte) 1 : (byte) 0);
            }
        } catch (Throwable e) {
            throw new AssignToStatementException(e);
        }
    }
}
