package com.qull.springarch.beans.factory;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 15:05
 */
public interface BeanFactory {
    Object getBean(String beanId);

    Class<?> getType(String name) throws NoSuchBeanDefinitionException;

    List<Object> getBeansByType(Class<?> type);
}
