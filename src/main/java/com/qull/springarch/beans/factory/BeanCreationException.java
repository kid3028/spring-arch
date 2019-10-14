package com.qull.springarch.beans.factory;

import com.qull.springarch.beans.exception.BeansException;
import lombok.Data;

import java.security.PublicKey;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 16:52
 */
@Data
public class BeanCreationException extends BeansException {
    /**
     * bean name
     */
    private String beanName;

    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanCreationException(String beanName, String msg) {
        super("Error creating bean with name : '" + beanName + "' : " + msg);
        this.beanName = beanName;
    }

   public BeanCreationException(String beanName, String msg, Throwable cause) {
        this(beanName, msg);
        initCause(cause);
   }
}
