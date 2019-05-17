package com.nianxy.hplex.aggregation;

import com.nianxy.hplex.FieldInfo;

/**
 * Created by nianxingyan on 17/8/25.
 */
public class AggregateFuncMax implements IAggregate {
    private String field;

    protected AggregateFuncMax(String field) {
        this.field = field;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public String getSQLStatement(FieldInfo fi) {
        return "max(" + fi.getColumnByField(field) + ")";
    }
}
