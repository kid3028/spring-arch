package com.qull.springarch.config;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 22:50
 */
public interface SingletonBeanRegistry {
    void registrySingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

}
