package com.qull.springarch.aop.framework;

import com.qull.springarch.aop.Advice;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 22:12
 */
public interface AopConfig {

    Class<?> getTargetClass();

    Object getTargetObject();

    void setTargetObject(Object targetObject);

    boolean isProxyTargetClass();

    Class<?>[] getProxiedInterfaces();

    boolean isInterfaceProxied(Class<?> proxiedInterface);

    List<Advice> getAdvices();

    void addAdvice(Advice beforeAdvice);

    List<Advice> getAdvices(Method method);
}
