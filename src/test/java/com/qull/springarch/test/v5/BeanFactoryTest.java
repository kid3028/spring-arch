package com.qull.springarch.test.v5;

import com.qull.springarch.aop.Advice;
import com.qull.springarch.aop.aspectj.AspectJBeforeAdvice;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.tx.TransactionManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 12:18
 */
public class BeanFactoryTest extends AbstractTest {

    static String expectedExpression = "execution(* com.qull.springarch.service.v5.*.placeOrder(..))";

    @Test
    public void testGetBeanByType() throws Exception {
        BeanFactory factory = super.getBeanFactory("petstore-v5.xml");
        List<Object> advices = factory.getBeansByType(Advice.class);

        Assert.assertEquals(3, advices.size());

        {
            AspectJBeforeAdvice advice = (AspectJBeforeAdvice) this.getAdvice(AspectJBeforeAdvice.class, advices);
            Assert.assertEquals(TransactionManager.class.getMethod("start"), advice.getAdviceMethod());
            Assert.assertEquals(expectedExpression, advice.getPointcut().getExpression());
            Assert.assertEquals(TransactionManager.class, advice.getAdviceInstance().getClass());
        }

    }

    public Object getAdvice(Class<?> type, List<Object> advises) {
        for (Object advise : advises) {
            if (advise.getClass().equals(type)) {
                return advise;
            }
        }
        return null;
    }

}
