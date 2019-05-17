package com.nianxy.hplex.aggregation;

import com.nianxy.hplex.FieldInfo;

/**
 * Created by nianxingyan on 17/8/25.
 */
public interface IAggregate {
    // if it's a aggregation funtion, return true
    boolean isFunction();

    String getSQLStatement(FieldInfo fi);
}
