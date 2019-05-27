package com.nianxy.hplex.assign;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class FloatAssigner implements ValueAssigner {
    @Override
    public void assign(Object obj, Field field, ResultSet rs, String label) throws Exception {
        Object v = rs.getObject(label);
        field.set(obj, v==null?null:rs.getFloat(label));
    }

    @Override
    public void assign(PreparedStatement pstmt, int idx, Object value) throws SQLException {
        if (value==null) {
            pstmt.setNull(idx, Types.FLOAT);
        } else {
            pstmt.setFloat(idx, ((Number) value).floatValue());
        }
    }
}