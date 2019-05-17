package com.nianxy.hplex;

import com.nianxy.hplex.assign.ValueAssigner;

/**
 * Created by nianxingyan on 17/8/17.
 */
public interface FieldInfo {
    String getColumnByField(String field);
    ValueAssigner getAssignerByField(String field);
}
