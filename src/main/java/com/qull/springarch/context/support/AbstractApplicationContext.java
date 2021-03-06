package com.qull.springarch.context.support;

import com.qull.springarch.aop.aspectj.AspectJAutoProxyCreator;
import com.qull.springarch.beans.factory.NoSuchBeanDefinitionException;
import com.qull.springarch.beans.factory.annotation.AutowiredAnnotationProcessor;
import com.qull.springarch.beans.factory.config.ConfigurableBeanFactory;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.context.ApplicationContext;
import com.qull.springarch.core.io.Resource;
import com.qull.springarch.util.ClassUtils;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 22:01
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    private DefaultBeanFactory factory;

    private ClassLoader beanClassLoader;

    public AbstractApplicationContext(String configFile) {
        factory = new DefaultBeanFactory();
        factory.setBeanClassLoader(this.getBeanClassLoader());
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(getResource(configFile));
        this.registerBeanProcessors(factory);
    }

    protected abstract Resource getResource(String configFile);

    @Override
    public Object getBean(String beanId) {
        return factory.getBean(beanId);
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return (this.beanClassLoader != null ? this.beanClassLoader : ClassUtils.getDefaultClassLoader());
    }

    protected void registerBeanProcessors(ConfigurableBeanFactory beanFactory) {
        {
            AutowiredAnnotationProcessor postProcessor = new AutowiredAnnotationProcessor();
            postProcessor.setBeanFactory(beanFactory);
            beanFactory.addBeanPostProcessor(postProcessor);
        }

        {
            AspectJAutoProxyCreator postProcessor = new AspectJAutoProxyCreator();
            postProcessor.setBeanFactory(beanFactory);
            beanFactory.addBeanPostProcessor(postProcessor);
        }
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return this.factory.getType(name);
    }

    @Override
    public List<Object> getBeansByType(Class<?> type) {
        return this.factory.getBeansByType(type);
    }
}
