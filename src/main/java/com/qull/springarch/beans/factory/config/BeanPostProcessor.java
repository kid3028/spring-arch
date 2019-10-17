package com.qull.springarch.beans.factory.config;

import com.qull.springarch.beans.exception.BeansException;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 14:05
 */
public interface BeanPostProcessor {
    Object beforeInitialization(Object bean, String beanName) throws BeansException;

    Object afterInitialization(Object bean, String beanName) throws BeansException;
}
