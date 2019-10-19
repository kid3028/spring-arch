package com.qull.springarch.aop;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 21:13
 */
public interface MethodMatcher {
    boolean matches(Method method);
}
