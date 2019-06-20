package com.nianxy.hplex.cond;

import com.nianxy.hplex.FieldInfo;
import com.nianxy.hplex.exception.AssignToStatementException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class CondCompare implements ICond {
    public enum Compare {
        GT, GTE, LT, LTE, EQ, NE
    }

    private Compare type;
    private String field;
    private Object value;

    protected CondCompare(Compare type, String field, Object value) {
        this.type = type;
        this.field = field;
        this.value = value;
    }

    protected String getCompareSymbol() {
        switch (type) {
            case GT: return ">";
            case GTE: return ">=";
            case LT: return "<";
            case LTE: return "<=";
            case EQ: return "=";
            case NE: return "!=";
        }
        return "";
    }

    @Override
    public String getWhereClouse(FieldInfo fi) {
        return fi.getColumnByField(field)+getCompareSymbol()+"?";
    }

    @Override
    public int setPrepareStatement(FieldInfo fi, PreparedStatement pstmt, int paramIndex) throws AssignToStatementException {
        fi.getAssignerByField(field).assign(pstmt, paramIndex++, value);
        return paramIndex;
    }
}
