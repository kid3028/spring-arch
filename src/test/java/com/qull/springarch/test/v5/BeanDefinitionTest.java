package com.qull.springarch.test.v5;

import com.qull.springarch.aop.aspectj.AspectJBeforeAdvice;
import com.qull.springarch.aop.aspectj.AspectJExpressionPointcut;
import com.qull.springarch.aop.config.AspectInstanceFactory;
import com.qull.springarch.aop.config.MethodLocatingFactory;
import com.qull.springarch.beans.ConstructorArgument;
import com.qull.springarch.beans.PropertyValue;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.config.RuntimeBeanReference;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.tx.TransactionManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 13:16
 */
public class BeanDefinitionTest extends AbstractTest {

    @Test
    public void testAOPBean() {
        DefaultBeanFactory factory = (DefaultBeanFactory) this.getBeanFactory("petstore-v5.xml");

        // 检查名称为tx的Bean的定义是否生效
        {
            BeanDefinition bd = factory.getBeanDefinition("tx");
            Assert.assertTrue(bd.getBeanClassName().equals(TransactionManager.class.getName()));

        }

        // 检查placeOrder是否正确生成
        {
            BeanDefinition bd = factory.getBeanDefinition("placeOrder");
            /// 这个BeanDefinition是合成的
            Assert.assertTrue(bd.isSynthetic());
            Assert.assertTrue(bd.getBeanClass().equals(AspectJExpressionPointcut.class));

            PropertyValue pv = bd.getPropertyValues().get(0);
            Assert.assertEquals("expression", pv.getName());
            Assert.assertEquals("execution(* com.qull.springarch.service.v5.*.placeOrder(..))", pv.getValue());
        }

        // 检查AspectJBeforeAdvice
        {
            String name = AspectJBeforeAdvice.class.getName() + "#0";
            BeanDefinition bd = factory.getBeanDefinition(name);
            Assert.assertTrue(bd.getBeanClass().equals(AspectJBeforeAdvice.class));

            // 这个BeanDefinition是合成的
            Assert.assertTrue(bd.isSynthetic());
            List<ConstructorArgument.ValueHolder> args = bd.getConstructorArgument().getArgumentValues();
            Assert.assertEquals(3, args.size());

            // 构造函数第一个参数
            {
                BeanDefinition innerBeanDef = (BeanDefinition) args.get(0).getValue();
                Assert.assertTrue(innerBeanDef.isSynthetic());
                Assert.assertTrue(innerBeanDef.getBeanClass().equals(MethodLocatingFactory.class));

                List<PropertyValue> pvs = innerBeanDef.getPropertyValues();
                Assert.assertEquals("targetBeanName", pvs.get(0).getName());
                Assert.assertEquals("tx", pvs.get(0).getValue());
                Assert.assertEquals("methodName", pvs.get(1).getName());
                Assert.assertEquals("start", pvs.get(1).getValue());
            }

            // 构造函数第二个参数
            {
                RuntimeBeanReference ref = (RuntimeBeanReference) args.get(1).getValue();
                Assert.assertEquals("placeOrder", ref.getBeanName());
            }

            //  构造函数第三个参数
            {
                BeanDefinition innerBeanDef = (BeanDefinition)args.get(2).getValue();
                Assert.assertTrue(innerBeanDef.isSynthetic());
                Assert.assertTrue(innerBeanDef.getBeanClass().equals(AspectInstanceFactory.class));

                List<PropertyValue> pvs = innerBeanDef.getPropertyValues();
                Assert.assertEquals("aspectBeanName", pvs.get(0).getName());
                Assert.assertEquals("tx", pvs.get(0).getValue());

            }
        }

    }
}
