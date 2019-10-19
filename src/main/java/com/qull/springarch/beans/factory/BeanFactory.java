package com.qull.springarch.beans.factory;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 15:05
 */
public interface BeanFactory {
    Object getBean(String beanId);

    Class<?> getType(String name) throws NoSuchBeanDefinitionException;
}
