package com.nianxy.hplex.assign;

import com.nianxy.hplex.HPlex;
import com.nianxy.hplex.IJSONConvert;
import com.nianxy.hplex.exception.AssignToFieldException;
import com.nianxy.hplex.exception.AssignToStatementException;
import com.nianxy.hplex.exception.NoJSONConvertException;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class JSONAssigner implements ValueAssigner {
    private IJSONConvert jsonConvert;
    private Class objectType;

    public JSONAssigner() throws NoJSONConvertException {
        if (HPlex.getConfigure().getJsonConvert()==null) {
            throw new NoJSONConvertException();
        }
        this.jsonConvert = HPlex.getConfigure().getJsonConvert();
    }

    public void setObjectType(Class objectType) {
        this.objectType = objectType;
    }

    /**
     * 将字符串转换为JSON对象后，赋给目标字段。可通过异常对象的getCause方法判断是否是JSON解析失败
     * @param obj 目标对象实例
     * @param field 对象的成员字段
     * @param rs 数据库结果集
     * @param label 数据库字段名称
     * @throws AssignToFieldException
     */
    @Override
    public void assign(Object obj, Field field, ResultSet rs, String label) throws AssignToFieldException {
        try {
            Object v = rs.getObject(label);
            field.set(obj, v==null?null:jsonConvert.toObject(rs.getString(label), objectType));
        } catch (Throwable e) {
            throw new AssignToFieldException(field, e);
        }
    }

    @Override
    public void assign(PreparedStatement pstmt, int idx, Object value) throws AssignToStatementException {
        try {
            if (value==null) {
                pstmt.setNull(idx, Types.CHAR);
            } else {
                pstmt.setString(idx, jsonConvert.toJSONString(value, objectType));
            }
        } catch (Throwable e) {
            throw new AssignToStatementException(e);
        }
    }
}
