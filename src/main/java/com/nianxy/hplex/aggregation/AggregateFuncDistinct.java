package com.nianxy.hplex.aggregation;

import com.nianxy.hplex.FieldInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianxingyan on 17/8/25.
 */
public class AggregateFuncDistinct implements IAggregate {
    private List<String> fields;

    protected AggregateFuncDistinct() {
        fields = new ArrayList<>();
    }

    public AggregateFuncDistinct add(String field) {
        fields.add(field);
        return this;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public String getSQLStatement(FieldInfo fi) {
        if (fields.size()==0)
            throw new RuntimeException("no field defined in AggregateFuncDistinct object");
        StringBuilder sb = new StringBuilder();
        sb.append("distinct(");
        for (String f:fields) {
            sb.append(fi.getColumnByField(f)).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }
}
