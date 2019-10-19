package com.qull.springarch.beans.factory;

import com.qull.springarch.beans.exception.BeansException;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 16:56
 */
public class BeanDefinitionStoreException extends BeansException {
    public BeanDefinitionStoreException(String message) {
        super(message);
    }

    public BeanDefinitionStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
