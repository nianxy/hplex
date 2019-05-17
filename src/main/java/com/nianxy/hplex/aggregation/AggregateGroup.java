package com.nianxy.hplex.aggregation;

import com.nianxy.hplex.FieldInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianxingyan on 17/8/25.
 */
public class AggregateGroup implements IAggregate {
    private List<String> fields;

    protected AggregateGroup() {
        fields = new ArrayList<>();
    }

    public AggregateGroup add(String field) {
        fields.add(field);
        return this;
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public String getSQLStatement(FieldInfo fi) {
        if (fields.size()==0)
            throw new RuntimeException("no field defined in AggregateGroup object");
        StringBuilder sb = new StringBuilder();
        sb.append(" group by ");
        for (String f:fields) {
            sb.append(fi.getColumnByField(f)).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }
}
