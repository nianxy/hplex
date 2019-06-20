package com.nianxy.hplex.cond;

import com.nianxy.hplex.FieldInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class CondIsNull implements ICond {
    private String field;

    protected CondIsNull(String field) {
        this.field = field;
    }

    @Override
    public String getWhereClouse(FieldInfo fi) {
        return fi.getColumnByField(field)+" is null";
    }

    @Override
    public int setPrepareStatement(FieldInfo fi, PreparedStatement pstmt, int paramIndex) {
        return paramIndex;
    }
}
