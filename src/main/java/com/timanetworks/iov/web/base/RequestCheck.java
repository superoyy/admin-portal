package com.timanetworks.iov.web.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 会话和权限检查注解
 * Created with IntelliJ IDEA.
 * User: dukla
 * Date: 13-8-5
 * Time: 下午1:54
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestCheck {
    public boolean checkSession() default false;
    public boolean checkPermit() default false;
}
