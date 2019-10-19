package com.qull.springarch.aop;

import org.junit.runners.Suite;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 10:14
 */
public class AopInvocationException extends RuntimeException {

    public AopInvocationException(String msg) {
        super(msg);
    }

    public AopInvocationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
