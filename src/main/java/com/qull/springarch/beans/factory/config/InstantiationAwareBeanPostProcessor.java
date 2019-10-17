package com.qull.springarch.beans.factory.config;

import com.qull.springarch.beans.exception.BeansException;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 14:04
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{
    Object beforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

    boolean afterInstantiation(Object bean, String beanName) throws BeansException;

    void postProcessPropertyValue(Object bean, String beanName) throws BeansException;
}
