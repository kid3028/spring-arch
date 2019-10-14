package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanDefinition;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 20:26
 */
public interface BeanDefinitionRegistry {
    BeanDefinition getBeanDefinition(String beanId);

    void registerBeanDefinition(String beanId, BeanDefinition bd);
}
