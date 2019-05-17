package com.nianxy.hplex.assign;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class IntegerAssigner implements ValueAssigner {
    @Override
    public void assign(Object obj, Field field, ResultSet rs, String label) throws Exception {
        field.set(obj, rs.getInt(label));
    }

    @Override
    public void assign(PreparedStatement pstmt, int idx, Object value) throws SQLException {
        if (value==null) {
            pstmt.setNull(idx, Types.INTEGER);
        } else {
            pstmt.setInt(idx, ((Number) value).intValue());
        }
    }
}
