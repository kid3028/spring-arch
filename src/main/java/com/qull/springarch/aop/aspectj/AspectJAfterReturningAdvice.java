package com.qull.springarch.aop.aspectj;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 10:44
 */
public class AspectJAfterReturningAdvice extends AbstractAspectJAdvice{
    public AspectJAfterReturningAdvice(Method adviceMethod, AspectJExpressionPointcut pointcut, Object adviceObject) {
        super(adviceMethod, pointcut, adviceObject);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object retult = methodInvocation.proceed();
        // 例如调用TransactionManager#commit()方法
        this.invokeAdviceMethod();
        return retult;
    }
}
