package com.qull.springarch.test.v5;

import com.qull.springarch.aop.MethodMatcher;
import com.qull.springarch.aop.aspectj.AspectJExpressionPointcut;
import com.qull.springarch.service.v5.PetStoreService;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 20:55
 */
public class PointcutTest {

    @Test
    public void testPointCut() throws Exception {
        String expression = "execution(* com.qull.springarch.service.v5.*.placeOrder(..))";

        AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
        pc.setExpression(expression);

        MethodMatcher mm = pc.getMethodMatcher();

        {
            Class<?> targetClass = PetStoreService.class;

            Method method1 = targetClass.getMethod("placeOrder");
            Assert.assertTrue(mm.matches(method1));

            Method method2 = targetClass.getMethod("getAccountDao");
            Assert.assertFalse(mm.matches(method2));
        }

        {
            Class<?> targetClass = PetStoreService.class;
            Method method = targetClass.getMethod("getAccountDao");
            Assert.assertFalse(mm.matches(method));
        }
    }
}
