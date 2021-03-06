package com.qull.springarch.test.v5;

import com.qull.springarch.aop.aspectj.AspectJAfterReturningAdvice;
import com.qull.springarch.aop.aspectj.AspectJAfterThrowingAdvice;
import com.qull.springarch.aop.aspectj.AspectJBeforeAdvice;
import com.qull.springarch.aop.aspectj.AspectJExpressionPointcut;
import com.qull.springarch.aop.config.AspectInstanceFactory;
import com.qull.springarch.aop.framework.AopConfig;
import com.qull.springarch.aop.framework.AopConfigSupport;
import com.qull.springarch.aop.framework.CglibProxyFactory;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.service.v5.PetStoreService;
import com.qull.springarch.tx.TransactionManager;
import com.qull.springarch.util.MessageTracker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 22:04
 */
public class CglibAopProxyTest extends AbstractTest {

    private static AspectJBeforeAdvice beforeAdvice = null;

    private static AspectJAfterReturningAdvice afterReturningAdvice = null;

    private static AspectJAfterThrowingAdvice afterThrowingAdvice = null;

    private BeanFactory beanFactory = null;

    private AspectInstanceFactory aspectInstanceFactory;

    private AspectJExpressionPointcut pc;

    @Before
    public void setUp() throws Exception {
        MessageTracker.clear();

        String expression = "execution(* com.qull.springarch.service.v5.*.placeOrder(..))";
        pc = new AspectJExpressionPointcut();
        pc.setExpression(expression);

        beanFactory = this.getBeanFactory("petstore-v5.xml");
        aspectInstanceFactory = this.getAspectInstanceFactory("tx");
        aspectInstanceFactory.setBeanFactory(beanFactory);

        beforeAdvice = new AspectJBeforeAdvice(TransactionManager.class.getMethod("start"), pc, aspectInstanceFactory);
        afterReturningAdvice = new AspectJAfterReturningAdvice(TransactionManager.class.getMethod("commit"), pc, aspectInstanceFactory);
    }

    @Test
    public void testGetPoxy() {
        AopConfig config = new AopConfigSupport();
        config.addAdvice(beforeAdvice);
        config.addAdvice(afterReturningAdvice);
        config.setTargetObject(new PetStoreService());

        CglibProxyFactory proxyFactory = new CglibProxyFactory(config);
        PetStoreService proxy = (PetStoreService) proxyFactory.getProxy();

        proxy.placeOrder();

        List<String> msgs = MessageTracker.getMsgs();
        Assert.assertEquals(3, msgs.size());
        Assert.assertEquals("start tx...", msgs.get(0));
        Assert.assertEquals("place order...", msgs.get(1));
        Assert.assertEquals("commit tx...", msgs.get(2));

        proxy.toString();

    }












}

