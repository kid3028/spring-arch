package com.qull.springarch.context.support;

import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.context.ApplicationContext;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 21:28
 */
public class ClassPathXmlApplicationContext implements ApplicationContext {

    private DefaultBeanFactory factory;

    public ClassPathXmlApplicationContext(String configFile) {
        factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(configFile);
    }

    @Override
    public Object getBean(String beanId) {
        return factory.getBean(beanId);
    }
}
