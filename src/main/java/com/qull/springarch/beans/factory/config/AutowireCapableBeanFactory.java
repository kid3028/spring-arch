package com.qull.springarch.beans.factory.config;

import com.qull.springarch.beans.factory.BeanFactory;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 7:19
 */
public interface AutowireCapableBeanFactory extends BeanFactory {
    Object resolveDependency(DependencyDescriptor descriptor);
}
