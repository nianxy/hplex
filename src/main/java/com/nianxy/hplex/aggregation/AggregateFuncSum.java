package com.nianxy.hplex.aggregation;

import com.nianxy.hplex.FieldInfo;

/**
 * Created by nianxingyan on 17/8/25.
 */
public class AggregateFuncSum implements IAggregate {
    private String field;

    protected AggregateFuncSum(String field) {
        this.field = field;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public String getSQLStatement(FieldInfo fi) {
        return "min(" + fi.getColumnByField(field) + ")";
    }
}
