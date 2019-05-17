package com.nianxy.hplex.order;

import com.nianxy.hplex.FieldInfo;

import java.util.Collection;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class Order {
    public static OrderASC ASC(String field) {
        return new OrderASC(field);
    }

    public static OrderDESC DESC(String field) {
        return new OrderDESC(field);
    }

    public static String getOrderClause(Collection<IOrder> orders, FieldInfo f2c) {
        if (orders!=null && orders.size()>0) {
            StringBuilder order = new StringBuilder();
            order.append(" order by ");
            orders.stream().forEach(s->{
                order.append(s.getOrderString(f2c)).append(",");
            });
            order.delete(order.length()-1, order.length());
            return order.toString();
        }
        return "";
    }
}
