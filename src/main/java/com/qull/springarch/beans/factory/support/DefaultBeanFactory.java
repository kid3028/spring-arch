package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.config.ConfigurableBeanFactory;
import com.qull.springarch.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 15:06
 */
public class DefaultBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry, ConfigurableBeanFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultBeanFactory.class);

    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public DefaultBeanFactory() {

    }

    @Override
    public BeanDefinition getBeanDefinition(String beanId) {
        return this.beanDefinitionMap.get(beanId);
    }

    @Override
    public void registerBeanDefinition(String beanId, BeanDefinition bd) {
        this.beanDefinitionMap.put(beanId, bd);
    }

    @Override
    public Object getBean(String beanId) {
        BeanDefinition bd = this.getBeanDefinition(beanId);
        if (bd == null) {
            log.error("bean {} not exists", beanId);
            throw new BeanCreationException("bean '" + beanId + "' not exists");
        }
        if(bd.isSingleton()) {
            Object bean = getSingleton(beanId);
            if(bean == null) {
                bean = createBean(bd);
                this.registrySingleton(beanId, bean);
            }
            return bean;
        }
        return createBean(bd);

    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.classLoader = beanClassLoader;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
    }

    private Object createBean(BeanDefinition bd) {
        ClassLoader c1 = this.getBeanClassLoader();
        String beanClassName = bd.getBeanClassName();
        try {
            Class<?> beanClass = c1.loadClass(beanClassName);
            return beanClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new BeanCreationException("create bean for '" + bd.getBeanClassName() + "' fail", e);
        }
    }
}
