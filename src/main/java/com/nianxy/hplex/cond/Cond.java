package com.nianxy.hplex.cond;

import com.nianxy.hplex.FieldInfo;
import com.nianxy.hplex.exception.AssignToStatementException;
import com.nianxy.hplex.exception.FieldNotFoundException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class Cond {
    /**
     * 为一个字段设置between条件
     * @param field 对象的字段名称，注意不是数据库字段名称
     * @return
     */
    public static CondBetween between(String field, Object min, Object max) {
        return new CondBetween(field, min, max);
    }

    /**
     * 为一个字段设置比较条件
     * @param type 比较算法
     * @param field 对象的字段名称，注意不是数据库字段名称
     * @return
     */
    public static CondCompare compare(CondCompare.Compare type, String field, Object value) {
        return new CondCompare(type, field, value);
    }

    /**
     * 判断字段为null
     * @param field 对象的字段名称，注意不是数据库字段名称
     * @return
     */
    public static CondIsNull isNull(String field) {
        return new CondIsNull(field);
    }

    /**
     * 判断字段不为null
     * @param field 对象的字段名称，注意不是数据库字段名称
     * @return
     */
    public static CondNotNull notNull(String field) {
        return new CondNotNull(field);
    }

    /**
     * 为一个字段设置like操作条件
     * @param field 对象的字段名称，注意不是数据库字段名称
     * @param like 查询串
     * @return
     */
    public static CondLike like(String field, String like) {
        return new CondLike(field, like);
    }

    /**
     * 为一个字段设置in操作条件
     * @param field 对象的字段名称，注意不是数据库字段名称
     * @return
     */
    public static CondIn in(String field) {
        return new CondIn(field);
    }

    /**
     * 设置自定义的where语句
     * @return
     */
    public static CondCustom custom() {
        return new CondCustom();
    }

    public static String getWhereClause(Collection<ICond> conds, FieldInfo fi) throws FieldNotFoundException {
        if (conds!=null && conds.size()>0) {
            StringBuilder where = new StringBuilder();
            where.append(" where ");
            for (ICond s : conds) {
                where.append(s.getWhereClouse(fi)).append(" and ");
            }
            where.delete(where.length()-5, where.length());
            return where.toString();
        }
        return "";
    }

    public static int setWherePrepareStatement(Collection<ICond> conds, PreparedStatement pstmt,
                                               int paramIndex, FieldInfo fi) throws AssignToStatementException, FieldNotFoundException {
        for (ICond cond:conds) {
            paramIndex = cond.setPrepareStatement(fi, pstmt, paramIndex);
        }
        return paramIndex;
    }
}
