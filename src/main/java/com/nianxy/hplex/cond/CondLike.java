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
public class CondLike implements ICond {
    private String field;
    private String like;

    protected CondLike(String field, String like) {
        this.field = field;
        this.like = like;
    }

    @Override
    public String getWhereClouse(FieldInfo fi) throws FieldNotFoundException {
        return fi.getColumnByField(field) + " like ? ";
    }

    @Override
    public int setPrepareStatement(FieldInfo fi, PreparedStatement pstmt, int paramIndex) throws AssignToStatementException, FieldNotFoundException {
        ValueAssigner va = fi.getAssignerByField(field);
        va.assign(pstmt, paramIndex++, like);
        return paramIndex;
    }
}
