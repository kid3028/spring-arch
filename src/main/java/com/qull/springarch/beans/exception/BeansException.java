package com.qull.springarch.beans.exception;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 16:50
 */
public class BeansException extends RuntimeException{
    public BeansException(String message) {
        super(message);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
}
