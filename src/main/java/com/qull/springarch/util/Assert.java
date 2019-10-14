package com.qull.springarch.util;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 16:07
 */
public abstract class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
