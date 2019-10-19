package com.qull.springarch.test.v5;

import com.qull.springarch.aop.config.AspectInstanceFactory;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.tx.TransactionManager;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 12:18
 */
public class AbstractTest {

    protected BeanFactory getBeanFactory(String configFile) {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new ClassPathResource("petstore-v5.xml"));
        return factory;
    }

    protected Method getAdviceMethod(String methodName) throws NoSuchMethodException {
        return TransactionManager.class.getMethod(methodName);
    }

    protected AspectInstanceFactory getAspectInstanceFactory(String targetBeanName) {
        AspectInstanceFactory factory = new AspectInstanceFactory();
        factory.setAspectBeanName(targetBeanName);
        return factory;
    }

}
