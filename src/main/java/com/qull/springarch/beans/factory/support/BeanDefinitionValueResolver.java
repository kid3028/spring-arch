package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.config.RuntimeBeanReference;
import com.qull.springarch.config.TypedStringValue;

import javax.management.loading.PrivateClassLoader;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 17:02
 */
public class BeanDefinitionValueResolver {
    private final BeanFactory factory;

    public BeanDefinitionValueResolver(BeanFactory factory) {
        this.factory = factory;
    }

    public Object resolveValueIfNecessary(Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            String refName = ref.getBeanName();
            return this.factory.getBean(refName);
        }else if (value instanceof TypedStringValue) {
            return ((TypedStringValue) value).getValue();
        }else {
            throw new RuntimeException("the value " + value + " has not implemented");
        }
    }
}
