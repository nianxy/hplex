package com.nianxy.hplex.order;

import com.nianxy.hplex.FieldInfo;
import com.nianxy.hplex.exception.FieldNotFoundException;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class OrderDESC implements IOrder {
    private String field;

    protected OrderDESC(String field) {
        this.field = field;
    }

    @Override
    public String getOrderString(FieldInfo f2c) throws FieldNotFoundException {
        return "`"+f2c.getColumnByField(field) + "` desc";
    }
}
