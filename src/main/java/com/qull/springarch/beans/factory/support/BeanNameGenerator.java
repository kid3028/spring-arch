package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanDefinition;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 21:27
 */
public interface BeanNameGenerator {

    String generateBeanName(BeanDefinition bd, BeanDefinitionRegistry registry);
}
