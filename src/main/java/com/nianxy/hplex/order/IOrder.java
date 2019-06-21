package com.nianxy.hplex.order;

import com.nianxy.hplex.FieldInfo;
import com.nianxy.hplex.exception.FieldNotFoundException;

/**
 * Created by nianxingyan on 17/8/17.
 */
public interface IOrder {
    String getOrderString(FieldInfo f2c) throws FieldNotFoundException;
}
