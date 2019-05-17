package com.nianxy.hplex.cond;

import com.nianxy.hplex.FieldInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by nianxingyan on 17/8/17.
 */
public interface ICond {
    /**
     * 将条件拼到where语句中，比如可以返回 id=?，
     * 之后在{@link #setPrepareStatement(FieldInfo, PreparedStatement,int)}中再将参数拼到PreparedStatement中
     * @return where子句中代表当前条件的描述
     */
    String getWhereClouse(FieldInfo fi);

    /**
     * 在PreparedStatement中设置参数，返回操作后的参数列表下标
     * @param fi
     * @param pstmt 连接的PreparedStatement对象
     * @param paramIndex 即将拼接的参数下标
     * @return 下一个要拼接的参数下标
     */
    int setPrepareStatement(FieldInfo fi, PreparedStatement pstmt, int paramIndex) throws SQLException;
}
