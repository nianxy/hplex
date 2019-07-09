package com.nianxy.hplex;

import app.nianxy.commonlib.exceptionutils.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;

/**
 * Created by nianxingyan on 17/8/16.
 */
public class HPlex {
    private static final Logger logger = LogManager.getLogger(HPlex.class);

    private static HPlexConfigure configure;

    public static void init(HPlexConfigure configure) {
        HPlex.configure = configure;
    }

    public static final HPlexConfigure getConfigure() {
        return configure;
    }

    public static TableInfo getTableInfo(Class<?> c) {
        return configure.getTableInfo(c);
    }

    public static void reportSQL(PreparedStatement pstmt) {
        IReceiveSQL receiver = configure.getReceiveSQL();
        if (receiver!=null) {
            Class clz = pstmt.getClass();
            try {
                Method method = clz.getMethod("asSql");
                receiver.beforeExecute((String)method.invoke(pstmt));
            } catch (NoSuchMethodException e) {
                Object delegate = pstmt;
                try {
                    while (true) {
                        Method method = clz.getMethod("getDelegate");
                        delegate = method.invoke(delegate);
                        clz = delegate.getClass();
                    }
                } catch (NoSuchMethodException ex) {
                    try {
                        Method method = delegate.getClass().getMethod("asSql");
                        receiver.beforeExecute((String)method.invoke(delegate));
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        logger.error("statement object has no asSql() method:" + ExceptionUtils.getTraceInfo(ex));
                    }
                } catch (Exception ex) {
                    logger.error("delegate statement object asSql() failed:" + ExceptionUtils.getTraceInfo(ex));
                }
            } catch (Exception e) {
                logger.error("statement object asSql() failed:" + ExceptionUtils.getTraceInfo(e));
            }
        }
    }
}
