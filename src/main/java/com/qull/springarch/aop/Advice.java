package com.qull.springarch.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 10:13
 */
public interface Advice extends MethodInterceptor {
    Pointcut getPointcut();

}
