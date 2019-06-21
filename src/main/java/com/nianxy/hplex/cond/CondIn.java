package com.nianxy.hplex.cond;

import com.nianxy.hplex.FieldInfo;
import com.nianxy.hplex.assign.ValueAssigner;
import com.nianxy.hplex.exception.AssignToStatementException;
import com.nianxy.hplex.exception.FieldNotFoundException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class CondIn implements ICond {
    private String field;
    private List<Object> valuelist;

    protected CondIn(String field) {
        this.field = field;
        valuelist = new ArrayList<>();
    }

    public CondIn addValue(Object value) {
        valuelist.add(value);
        return this;
    }

    public CondIn addValues(Collection values) {
        valuelist.addAll(values);
        return this;
    }

    @Override
    public String getWhereClouse(FieldInfo fi) throws FieldNotFoundException {
        if (valuelist.size()==0) {
            throw new RuntimeException("no value defined in CondIn object");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(fi.getColumnByField(field)).append(" in (");
        for (int i = 0; i < valuelist.size(); ++i) {
            sb.append("?,");
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }

    @Override
    public int setPrepareStatement(FieldInfo fi, PreparedStatement pstmt, int paramIndex) throws AssignToStatementException, FieldNotFoundException {
        ValueAssigner va = fi.getAssignerByField(field);
        for (Object v:valuelist) {
            va.assign(pstmt, paramIndex++, v);
        }
        return paramIndex;
    }
}
