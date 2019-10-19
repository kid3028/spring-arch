package com.qull.springarch.test.v5;

import com.qull.springarch.aop.aspectj.AspectJAfterReturningAdvice;
import com.qull.springarch.aop.aspectj.AspectJAfterThrowingAdvice;
import com.qull.springarch.aop.aspectj.AspectJBeforeAdvice;
import com.qull.springarch.aop.aspectj.AspectJExpressionPointcut;
import com.qull.springarch.aop.config.AspectInstanceFactory;
import com.qull.springarch.aop.framework.ReflectiveMethodInvocation;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.service.v5.PetStoreService;
import com.qull.springarch.tx.TransactionManager;
import com.qull.springarch.util.MessageTracker;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 10:46
 */
public class ReflectiveMethodInvocationTest extends AbstractTest{

    private AspectJBeforeAdvice beforeAdvice = null;

    private AspectJAfterReturningAdvice afterReturningAdvice = null;

    private AspectJAfterThrowingAdvice afterThrowingAdvice = null;

    private AspectJExpressionPointcut pointcut = null;

    private BeanFactory beanFactory = null;

    private AspectInstanceFactory aspectInstanceFactory = null;

    private PetStoreService petStoreService = null;

    private TransactionManager tx;

    @Before
    public void setup() throws NoSuchMethodException {
        petStoreService = new PetStoreService();
        tx = new TransactionManager();

        MessageTracker.clear();

        beanFactory =this.getBeanFactory("petstore.xml");
        aspectInstanceFactory = this.getAspectInstanceFactory("tx");
        aspectInstanceFactory.setBeanFactory(beanFactory);

        beforeAdvice = new AspectJBeforeAdvice(this.getAdviceMethod("start"), null, aspectInstanceFactory);
        afterReturningAdvice = new AspectJAfterReturningAdvice(this.getAdviceMethod("commit"), null, aspectInstanceFactory);
        afterThrowingAdvice = new AspectJAfterThrowingAdvice(this.getAdviceMethod("rollback"), null, aspectInstanceFactory);
    }

    @Test
    public void testMethodInvocation() throws Throwable {
        Method targetMethod = petStoreService.getClass().getMethod("placeOrder");

        List<MethodInterceptor> interceptors = new ArrayList<>();
        interceptors.add(beforeAdvice);
        interceptors.add(afterReturningAdvice);

        ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStoreService, targetMethod, new Object[0], interceptors);

        mi.proceed();

        List<String> msgs = MessageTracker.getMsgs();
        Assert.assertEquals(3, msgs.size());
        Assert.assertEquals("start tx...", msgs.get(0));
        Assert.assertEquals("place order...", msgs.get(1));
        Assert.assertEquals("commit tx...", msgs.get(2));

    }


    @Test
    public void testMethodInvocationThrowing() throws Throwable {
        Method targetMethod = petStoreService.getClass().getMethod("placeOrderWithException");

        List<MethodInterceptor> interceptors = new ArrayList<>();
        interceptors.add(afterThrowingAdvice);
        interceptors.add(beforeAdvice);

        ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStoreService, targetMethod, new Object[0], interceptors);

        try {
            mi.proceed();
        } catch (Throwable t) {
            List<String> msgs = MessageTracker.getMsgs();
            Assert.assertEquals(2, msgs.size());
            Assert.assertEquals("start tx...", msgs.get(0));
            Assert.assertEquals("rollback tx...", msgs.get(1));
            return;
        }

        Assert.fail();

    }

}
