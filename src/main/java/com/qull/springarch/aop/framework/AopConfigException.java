package com.qull.springarch.aop.framework;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 23:15
 */
public class AopConfigException extends RuntimeException {

    public AopConfigException(String message) {
        super(message);
    }

    public AopConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
