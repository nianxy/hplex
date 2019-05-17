package com.nianxy.hplex.assign;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class DateAssigner implements ValueAssigner {
    private static final String DateFormatString = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void assign(Object obj, Field field, ResultSet rs, String label) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormatString);
        String t = rs.getString(label);
        if (t!=null) {
            field.set(obj, sdf.parse(t));
        } else {
            field.set(obj, null);
        }
    }

    @Override
    public void assign(PreparedStatement pstmt, int idx, Object value) throws SQLException {
        if (value==null) {
            pstmt.setNull(idx, Types.DATE);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormatString);
            pstmt.setString(idx, sdf.format((Date) value));
        }
    }
}
