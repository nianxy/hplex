package com.nianxy.hplex.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nianxingyan on 17/8/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String value() default "";

    /**
     * 被标记为AutoIncrement的字段，在插入时默认不被设置在字段列表中
     * @return
     */
    boolean autoinc() default false;
}
