package com.nianxy.hplex;

import com.nianxy.hplex.exception.JSONConvertException;

public interface IJSONConvert {
    /**
     * 将JSON串转换为对象，必须是线程安全的
     * @param jsonString
     * @param clz
     * @param <T>
     * @return
     */
    <T extends IJSONColumn>T toObject(String jsonString, Class<T> clz) throws JSONConvertException;

    /**
     * 将对象转换为JSON串，必须是线程安全的
     * @param object
     * @param clz
     * @return
     */
    <T extends IJSONColumn>String toJSONString(Object object, Class<T> clz) throws JSONConvertException;
}
