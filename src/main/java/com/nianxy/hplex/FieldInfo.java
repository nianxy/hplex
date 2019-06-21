package com.nianxy.hplex;

import com.nianxy.hplex.assign.ValueAssigner;
import com.nianxy.hplex.exception.FieldNotFoundException;

/**
 * Created by nianxingyan on 17/8/17.
 */
public interface FieldInfo {
    String getColumnByField(String field) throws FieldNotFoundException;
    ValueAssigner getAssignerByField(String field) throws FieldNotFoundException;
}
