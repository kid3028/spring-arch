package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanDefinition;

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

    @Override
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override
    public boolean isPrototype() {
        return this.prototype;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope) || SCOPE_DEFAULT.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

    @Override
    public String getScope() {
        return this.scope;
    }
}
