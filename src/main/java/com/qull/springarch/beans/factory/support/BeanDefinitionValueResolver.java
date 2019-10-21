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

    /**
     * 解析属性
     * @param value
     * @return
     */
    public Object resolveValueIfNecessary(Object value) {
        // 运行时
        if (value instanceof RuntimeBeanReference) {
            // 根据属性名，从BeanFactory中找到对应的bean
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            String refName = ref.getBeanName();
            return this.factory.getBean(refName);
        }
        // 字面值属性
        else if (value instanceof TypedStringValue) {
            return ((TypedStringValue) value).getValue();
        }
        // BeanDefinition属性
        else if (value instanceof BeanDefinition) {
            BeanDefinition bd = (BeanDefinition) value;
            // 形成内部bean
            String innerBeanName = "(inner bean)" + bd.getBeanClassName() + "#" + Integer.toHexString(System.identityHashCode(bd));
            return resolveInnerBean(innerBeanName, bd);
        }else {
            return value;
        }
    }

    private Object resolveInnerBean(String innerBeanName, BeanDefinition innerBd) {
        try {
            // 解析内部bean
            Object innerBean = this.factory.createBean(innerBd);
            //  如果是FactoryBean的实例，调用getObject()方法获取实际对象
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
