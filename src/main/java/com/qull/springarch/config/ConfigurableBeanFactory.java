package com.qull.springarch.config;

import com.qull.springarch.beans.factory.BeanFactory;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 22:22
 */
public interface ConfigurableBeanFactory extends BeanFactory {
    void setBeanClassLoader(ClassLoader beanClassLoader);
    ClassLoader getBeanClassLoader();
}
