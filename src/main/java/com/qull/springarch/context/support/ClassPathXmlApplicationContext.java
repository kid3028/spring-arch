package com.qull.springarch.context.support;

import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.context.ApplicationContext;
import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.core.io.Resource;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 21:28
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    public ClassPathXmlApplicationContext(String configFile) {
        super(configFile);
    }

    @Override
    protected Resource getResource(String configFile) {
        return new ClassPathResource(configFile, this.getBeanClassLoader());
    }
}
