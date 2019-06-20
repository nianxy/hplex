package com.nianxy.hplex.assign;

import com.nianxy.hplex.exception.AssignToFieldException;
import com.nianxy.hplex.exception.AssignToStatementException;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nianxingyan on 17/8/16.
 */
public interface ValueAssigner {
    /**
     * 为对象的字段赋值
     * @param obj 目标对象实例
     * @param field 对象的成员字段
     * @param rs 数据库结果集
     * @param label 数据库字段名称
     * @throws Exception
     */
    void assign(Object obj, Field field, ResultSet rs, String label) throws AssignToFieldException;

    /**
     * 将对象字段设置到Statement中
     * @param pstmt
     * @param idx
     * @param value
     * @throws Exception
     */
    void assign(PreparedStatement pstmt, int idx, Object value) throws AssignToStatementException;
}
