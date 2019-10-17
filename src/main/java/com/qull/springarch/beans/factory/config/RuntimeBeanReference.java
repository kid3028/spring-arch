package com.qull.springarch.beans.factory.config;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 16:36
 */
public class RuntimeBeanReference {
    private final String beanName;

    public RuntimeBeanReference(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return this.beanName;
    }
}
