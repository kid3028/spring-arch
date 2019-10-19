package com.qull.springarch.test.v5;

import com.qull.springarch.aop.config.MethodLocatingFactory;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.tx.TransactionManager;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 7:35
 */
public class MethodLocatingFactoryTest {

    @Test
    public void testGetMethod() throws NoSuchMethodException {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new ClassPathResource("petstore-v5.xml"));

        MethodLocatingFactory methodLocatingFactory = new MethodLocatingFactory();
        methodLocatingFactory.setTargetBeanName("tx");
        methodLocatingFactory.setMethodName("start");
        methodLocatingFactory.setBeanFactory(factory);

        Method method = methodLocatingFactory.getObject();

        Assert.assertTrue(TransactionManager.class.equals(method.getDeclaringClass()));
        Assert.assertTrue(TransactionManager.class.getMethod("start").equals(method));
    }
}
