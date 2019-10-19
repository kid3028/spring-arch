package com.qull.springarch.aop.aspectj;

import com.qull.springarch.aop.Advice;
import com.qull.springarch.aop.Pointcut;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 10:16
 */
public abstract class AbstractAspectJAdvice implements Advice {

    /**
     * 通知方法 TransactionManager#start
     */
    protected Method adviceMethod;

    /**
     * 切入点
     */
    protected AspectJExpressionPointcut pointcut;

    /**
     * 通知对象 TransactionManager
     */
    protected Object adviceObject;

    public AbstractAspectJAdvice(Method adviceMethod, AspectJExpressionPointcut pointcut, Object adviceObject) {
        this.adviceMethod = adviceMethod;
        this.pointcut = pointcut;
        this.adviceObject = adviceObject;
    }

    public void invokeAdviceMethod() throws Throwable {
        adviceMethod.invoke(adviceObject);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    public Method getAdviceMethod() {
        return this.adviceMethod;
    }
}
