package com.nianxy.hplex.aggregation;

import com.nianxy.hplex.FieldInfo;

import java.util.List;

/**
 * Created by nianxingyan on 17/8/25.
 */
public class Aggregate {
    public static AggregateFuncMax max(String field) {
        return new AggregateFuncMax(field);
    }

    public static AggregateFuncMin min(String field) {
        return new AggregateFuncMin(field);
    }

    public static AggregateFuncSum sum(String field) {
        return new AggregateFuncSum(field);
    }

    public static AggregateFuncDistinct distinct() {
        return new AggregateFuncDistinct();
    }

    public static AggregateGroup group() {
        return new AggregateGroup();
    }

    public static String getAggregateFuncString(List<IAggregate> funcs, FieldInfo fi) {
        if (funcs!=null && funcs.size()>0) {
            StringBuilder sql = new StringBuilder();
            funcs.stream().forEach(f->{
                sql.append(f.getSQLStatement(fi)).append(",");
            });
            sql.deleteCharAt(sql.length()-1);
            return sql.toString();
        }
        return "";
    }
}
