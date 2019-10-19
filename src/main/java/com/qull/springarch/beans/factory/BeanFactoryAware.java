package com.qull.springarch.beans.factory;

import com.qull.springarch.beans.exception.BeansException;


/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 12:25
 */
public interface BeanFactoryAware {

    /**
     * Callback that supplies the owning factory to a bean instance.
     * Invoked after the population of normal bean properties but before an initialization
     * callback such as {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
     *
     * @param beanFactory owning BeanFactory (never {@code null}). The bean can immediately call method on the factory
     * @throws BeansException
     */
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
