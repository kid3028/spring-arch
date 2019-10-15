package com.qull.springarch.beans.exception;

import lombok.Getter;
import lombok.Value;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 15:00
 */
@Getter
public class TypeMismatchException extends BeansException{

    private transient Object value;

    private Class<?> requiredType;

    public TypeMismatchException(Object value, Class<?> requiredType) {
        super("Failed to convert value : " + value + " to type " + requiredType);
        this.value = value;
        this.requiredType = requiredType;
    }

    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
