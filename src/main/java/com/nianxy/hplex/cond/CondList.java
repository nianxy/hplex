package com.nianxy.hplex.cond;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CondList {
    private static Logger logger = LogManager.getLogger(CondList.class);

    public static CondList from(ICond cond) {
        return new CondList().addCond(cond);
    }

    private List<ICond> conds;

    public CondList() {
        conds = new ArrayList<>();
    }

    public CondList addCond(ICond cond) {
        conds.add(cond);
        return this;
    }

    public List<ICond> getConds() {
        return conds;
    }
}
