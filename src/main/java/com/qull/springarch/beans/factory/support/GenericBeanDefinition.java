package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanDefinition;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 16:32
 */
public class GenericBeanDefinition implements BeanDefinition {
    /**
     * bean id
     */
    private String id;

    /**
     * bean className
     */
    private String beanClassName;

    /**
     * bean class
     */
    private Class<?> beanClass;

    private boolean singleton = true;

    private boolean prototype = false;

    private String scope = SCOPE_DEFAULT;

    public GenericBeanDefinition() {}

    public GenericBeanDefinition(String id, String beanClassName) {
        this.id = id;
        this.beanClassName = beanClassName;
    }

    @Override
    public String getBeanClassName() {
        return this.beanClassName;
    }
}
