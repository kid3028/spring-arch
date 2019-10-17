package com.qull.springarch.beans.factory.config;

import com.qull.springarch.beans.factory.BeanFactory;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 22:22
 */
public interface ConfigurableBeanFactory extends AutowireCapableBeanFactory {
    void setBeanClassLoader(ClassLoader beanClassLoader);

    ClassLoader getBeanClassLoader();

    void addBeanPostProcessor(BeanPostProcessor postProcessor);

    List<BeanPostProcessor> getBeanPostProcessors();
}
