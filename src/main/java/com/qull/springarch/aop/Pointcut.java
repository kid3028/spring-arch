package com.qull.springarch.aop;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 21:45
 */
public interface Pointcut {
    MethodMatcher getMethodMatcher();

    String getExpression();

}
