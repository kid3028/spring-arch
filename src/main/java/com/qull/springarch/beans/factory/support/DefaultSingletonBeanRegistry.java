package com.qull.springarch.beans.factory.support;

import com.qull.springarch.config.SingletonBeanRegistry;
import com.qull.springarch.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 22:52
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private static final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(64);

    @Override
    public void registrySingleton(String beanName, Object singletonObject) {
        Assert.notNull(beanName, "beanName must not be null");
        Object oldObj = singletonObjects.get(beanName);
        if (oldObj != null) {
            throw new IllegalStateException("cannot registry object '" + singletonObject + "' under bean name '" + beanName + "',there is already object : " + oldObj);
        }
        singletonObjects.putIfAbsent(beanName, singletonObject);
    }

    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }
}
