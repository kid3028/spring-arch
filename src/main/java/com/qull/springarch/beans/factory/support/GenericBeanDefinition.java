package com.qull.springarch.beans.factory.support;

import com.qull.springarch.aop.config.MethodLocatingFactory;
import com.qull.springarch.beans.ConstructorArgument;
import com.qull.springarch.beans.PropertyValue;
import com.qull.springarch.beans.factory.BeanDefinition;

import java.util.ArrayList;
import java.util.List;

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

    List<PropertyValue> propertyValues = new ArrayList<>();

    ConstructorArgument constructorArgument = new ConstructorArgument();

    /**
     * 表明这个Bean定义是不是我们自己合成的
     */
    private boolean isSynthetic = false;

    public GenericBeanDefinition(Class<?> clazz) {
        this.beanClass = clazz;
        this.beanClassName = clazz.getName();
    }

    public GenericBeanDefinition() {}

    public GenericBeanDefinition(String id, String beanClassName) {
        this.id = id;
        this.beanClassName = beanClassName;
    }

    @Override
    public boolean isSynthetic() {
        return this.isSynthetic;
    }

    public void setSynthetic(boolean isSynthetic) {
        this.isSynthetic = isSynthetic;
    }


    @Override
    public String getBeanClassName() {
        return this.beanClassName;
    }

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
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

    public List<PropertyValue> getPropertyValues() {
        return this.propertyValues;
    }

    @Override
    public ConstructorArgument getConstructorArgument() {
        return this.constructorArgument;
    }

    @Override
    public String getBeanId() {
        return this.id;
    }

    @Override
    public void setId(String beanName) {
        this.id = beanName;
    }

    @Override
    public boolean hasConstructorArgumentValues() {
        return !this.constructorArgument.isEmpty();
    }

    @Override
    public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
        String className = getBeanClassName();
        if (className == null) {
            return null;
        }
        Class<?> resolvedClass = classLoader.loadClass(className);
        this.beanClass = resolvedClass;
        return resolvedClass;
    }

    @Override
    public Class<?> getBeanClass() throws IllegalStateException {
        return this.beanClass;
    }

    @Override
    public boolean hasBeanClass() {
        return this.beanClass != null;
    }


}
