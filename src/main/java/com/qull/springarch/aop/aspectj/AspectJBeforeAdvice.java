package com.qull.springarch.aop.aspectj;

import com.qull.springarch.aop.config.AspectInstanceFactory;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 10:25
 */
public class AspectJBeforeAdvice extends AbstractAspectJAdvice {
    public AspectJBeforeAdvice(Method adviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory adviceObjectFactory) {
        super(adviceMethod, pointcut, adviceObjectFactory);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // 例如调用TransactionManager#start
        this.invokeAdviceMethod();
        return methodInvocation.proceed();
    }
}
