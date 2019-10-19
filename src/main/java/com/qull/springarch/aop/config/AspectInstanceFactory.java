package com.qull.springarch.aop.config;

import com.qull.springarch.beans.exception.BeansException;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.beans.factory.BeanFactoryAware;
import com.qull.springarch.util.StringUtils;

/**
 * Implementation of {@link AspectInstanceFactory} that locates the aspect from the {@link BeanFactory}
 * using a configured bean name
 * @author kzh
 * @description
 * @DATE 2019/10/19 12:24
 */
public class AspectInstanceFactory implements BeanFactoryAware {

    private String aspectBeanName;

    private BeanFactory beanFactory;

    public void setAspectBeanName(String aspectBeanName) {
        this.aspectBeanName = aspectBeanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (!StringUtils.hasText(this.aspectBeanName)) {
            throw new IllegalArgumentException("'aspectBeanName' is required");
        }
    }

    public Object getAspectInstance() throws Exception {
        return this.beanFactory.getBean(this.aspectBeanName);
    }

}
