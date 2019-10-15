package com.qull.springarch.beans;

import com.sun.corba.se.impl.io.TypeMismatchException;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 11:07
 */
public interface TypeConverter {
    <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException;
}
