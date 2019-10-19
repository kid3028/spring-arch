package com.qull.springarch.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 10:27
 */
public class ReflectiveMethodInvocation implements MethodInvocation {

    /**
     * petStoreService
     */
    protected final Object targetObject;

    /**
     * placeOrder方法
     */
    protected final Method targetMethod;

    protected Object[] arguments;

    /**
     * list of MethodInterceptor
     */
    protected final List<MethodInterceptor> interceptors;

    /**
     * Index from 0 of the current interceptor we're invoking.
     * -1 until we invoke: then the current interceptor
     */
    private int currentInterceptorIndex = -1;

    public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments, List<MethodInterceptor> interceptors) {
        this.targetObject = target;
        this.targetMethod = method;
        this.arguments = arguments;
        this.interceptors = interceptors;
    }

    /**
     * Return the method invoked on the proxied interface.
     * May or may not correspond with method invoked on an underlying implementation of the interface
     * @return
     */
    @Override
    public Method getMethod() {
        return this.targetMethod;
    }

    @Override
    public Object[] getArguments() {
        return (this.arguments != null ? this.arguments : new Object[0]);
    }

    @Override
    public Object proceed() throws Throwable {
        // 所有的拦截器都已经调用完毕
        if (this.currentInterceptorIndex == this.interceptors.size() - 1) {
            return invokeJoinpoint();
        }
        this.currentInterceptorIndex++;
        MethodInterceptor interceptor = this.interceptors.get(this.currentInterceptorIndex);

        return interceptor.invoke(this);
    }

    /**
     * Invoke the joinpoint using reflection.
     * subclass can override this to use custom invocation
     * @return the return value of the joinpoint
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected Object invokeJoinpoint() throws InvocationTargetException, IllegalAccessException {
        return this.targetMethod.invoke(this.targetObject, this.arguments);
    }

    @Override
    public Object getThis() {
        return this.targetObject;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return this.targetMethod;
    }
}
