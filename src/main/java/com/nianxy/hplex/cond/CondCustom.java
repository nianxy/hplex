package com.nianxy.hplex.cond;

import com.nianxy.hplex.FieldInfo;
import com.nianxy.hplex.exception.AssignToStatementException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class CondCustom implements ICond {
    private String clause;
    private List<Object> valuelist;

    protected CondCustom() {
    }

    public CondCustom setClause(String clause) {
        this.clause = clause;
        return this;
    }

    public CondCustom addValue(Object value) {
        if (valuelist==null) {
            valuelist = new ArrayList<>();
        }
        valuelist.add(value);
        return this;
    }

    @Override
    public String getWhereClouse(FieldInfo fi) {
        return clause;
    }

    @Override
    public int setPrepareStatement(FieldInfo fi, PreparedStatement pstmt, int paramIndex) throws AssignToStatementException {
        try {
            for (Object v : valuelist) {
                pstmt.setString(paramIndex++, v.toString());
            }
        } catch (SQLException e) {
            throw new AssignToStatementException(e);
        }
        return paramIndex;
    }
}
