package com.nianxy.hplex;

import org.apache.log4j.Logger;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class HPlex {
    private static final Logger logger = Logger.getLogger(HPlex.class);

    private static HPlexConfigure configure;

    public static void init(HPlexConfigure configure) {
        HPlex.configure = configure;
    }

    protected static HPlexConfigure getConfigure() {
        return configure;
    }

    public static TableInfo getTableInfo(Class<?> c) {
        return configure.getTableInfo(c);
    }
}
