package com.qull.springarch.aop.framework;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 23:15
 */
public class AopconfigException extends RuntimeException {
    public AopconfigException(String message) {
        super(message);
    }

    public AopconfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
