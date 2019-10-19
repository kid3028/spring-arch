package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.config.ConfigurableBeanFactory;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 23:00
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    protected abstract Object createBean(BeanDefinition bd) throws BeanCreationException;
}
