package com.qull.springarch.aop.config;

import com.qull.springarch.beans.BeanUtils;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.beans.factory.BeanFactoryAware;
import com.qull.springarch.beans.factory.FactoryBean;
import com.qull.springarch.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 7:41
 */
public class MethodLocatingFactory implements FactoryBean<Method>, BeanFactoryAware {

    private String targetBeanName;

    private String methodName;

    private Method method;

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setBeanFactory(BeanFactory factory) {
        if (!StringUtils.hasText(this.targetBeanName)) {
            throw new IllegalArgumentException("Property 'targetBeanName' is required");
        }

        if (!StringUtils.hasText(this.methodName)) {
            throw new IllegalArgumentException("Property 'methodName' is required");
        }

        Class<?> beanClass = factory.getType(this.targetBeanName);
        if (beanClass == null) {
            throw new IllegalArgumentException("Can't determine type of bean with name '" + this.targetBeanName + "'");
        }

        this.method = BeanUtils.resolveSignature(this.methodName, beanClass);

        if (this.method == null) {
            throw new IllegalArgumentException("Unable to locate method [" + this.methodName + "] on bean [" + this.targetBeanName + "]");
        }
    }

    @Override
    public Method getObject() {
        return this.method;
    }

    @Override
    public Class<?> getObjectType() {
        return method.getClass();
    }
}
