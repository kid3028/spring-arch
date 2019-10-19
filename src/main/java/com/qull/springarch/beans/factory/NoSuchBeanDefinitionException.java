package com.qull.springarch.beans.factory;

import com.qull.springarch.beans.exception.BeansException;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 7:47
 */
public class NoSuchBeanDefinitionException extends BeansException {
    private String beanId;

    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }

    public NoSuchBeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBeanDefinitionException(String beanId, String message) {
        super(beanId + " is not found");
        this.beanId = beanId;
    }

}
