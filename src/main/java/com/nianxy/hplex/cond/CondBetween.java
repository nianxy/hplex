package com.nianxy.hplex.cond;

import com.nianxy.hplex.FieldInfo;
import com.nianxy.hplex.assign.ValueAssigner;
import com.nianxy.hplex.exception.AssignToStatementException;
import com.nianxy.hplex.exception.FieldNotFoundException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class CondBetween implements ICond {
    private String field;
    private Object min;
    private Object max;

    protected CondBetween(String field, Object min, Object max) {
        this.field = field;
        this.min = min;
        this.max = max;
    }

    @Override
    public String getWhereClouse(FieldInfo fi) throws FieldNotFoundException {
        return fi.getColumnByField(field) + " between ? and ?";
    }

    @Override
    public int setPrepareStatement(FieldInfo fi, PreparedStatement pstmt, int paramIndex) throws AssignToStatementException, FieldNotFoundException {
        ValueAssigner va = fi.getAssignerByField(field);
        va.assign(pstmt, paramIndex++, min);
        va.assign(pstmt, paramIndex++, max);
        return paramIndex;
    }
}
