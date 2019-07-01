package com.nianxy.hplex.assign;

import com.nianxy.hplex.exception.AssignToFieldException;
import com.nianxy.hplex.exception.AssignToStatementException;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class DateAssigner implements ValueAssigner {
    private static final String DateFormatString = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void assign(Object obj, Field field, ResultSet rs, String label) throws AssignToFieldException {
        try {
            field.set(obj, rs.getObject(label));
        } catch (Throwable e) {
            throw new AssignToFieldException(field, e);
        }
    }

    @Override
    public void assign(PreparedStatement pstmt, int idx, Object value) throws AssignToStatementException {
        try {
            if (value==null) {
                pstmt.setNull(idx, Types.DATE);
            } else {
                pstmt.setDate(idx, new java.sql.Date(((Date)value).getTime()));
            }
        } catch (Throwable e) {
            throw new AssignToStatementException(e);
        }
    }
}
