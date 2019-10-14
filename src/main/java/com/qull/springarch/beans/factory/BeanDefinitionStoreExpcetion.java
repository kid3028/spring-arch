package com.qull.springarch.beans.factory;

import com.qull.springarch.beans.exception.BeansException;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 16:56
 */
public class BeanDefinitionStoreExpcetion extends BeansException {
    public BeanDefinitionStoreExpcetion(String message) {
        super(message);
    }

    public BeanDefinitionStoreExpcetion(String message, Throwable cause) {
        super(message, cause);
    }
}
