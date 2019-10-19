package com.qull.springarch.beans.factory;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 22:41
 */
public interface FactoryBean<T> {

    T getObject() throws Exception;

    Class<?> getObjectType();
}
