package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.exception.BeansException;
import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.beans.factory.FactoryBean;
import com.qull.springarch.beans.factory.config.RuntimeBeanReference;
import com.qull.springarch.beans.factory.config.TypedStringValue;
import jdk.nashorn.internal.ir.IfNode;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 17:02
 */
public class BeanDefinitionValueResolver {
    private final AbstractBeanFactory factory;

    public BeanDefinitionValueResolver(AbstractBeanFactory factory) {
        this.factory = factory;
    }

    public Object resolveValueIfNecessary(Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            String refName = ref.getBeanName();
            return this.factory.getBean(refName);
        } else if (value instanceof TypedStringValue) {
            return ((TypedStringValue) value).getValue();
        } else if (value instanceof BeanDefinition) {
            BeanDefinition bd = (BeanDefinition) value;
            String innerBeanName = "(inner bean)" + bd.getBeanClassName() + "#" + Integer.toHexString(System.identityHashCode(bd));
            return resolveInnerBean(innerBeanName, bd);
        }else {
            return value;
        }
    }

    private Object resolveInnerBean(String innerBeanName, BeanDefinition innerBd) {
        try {
            Object innerBean = this.factory.createBean(innerBd);
            if(innerBean instanceof FactoryBean) {
                try {
                    return ((FactoryBean<?>) innerBean).getObject();
                } catch (Exception e) {
                    throw new BeanCreationException(innerBeanName, "FactoryBean threw exception on object creation", e);
                }
            }else {
                return innerBean;
            }
        }catch (BeansException e) {
            throw new BeanCreationException(innerBeanName, " Cannot create inner bean '" + innerBeanName + "' " + (innerBd != null && innerBd.getBeanClassName() != null ? "of type [" + innerBd.getBeanClassName() + "] " : ""), e );
        }
    }
}
