package com.qull.springarch.aop.aspectj;

import com.qull.springarch.aop.Advice;
import com.qull.springarch.aop.MethodMatcher;
import com.qull.springarch.aop.Pointcut;
import com.qull.springarch.aop.framework.AopConfigSupport;
import com.qull.springarch.aop.framework.AopProxyFactory;
import com.qull.springarch.aop.framework.CglibProxyFactory;
import com.qull.springarch.beans.exception.BeansException;
import com.qull.springarch.beans.factory.config.BeanPostProcessor;
import com.qull.springarch.beans.factory.config.ConfigurableBeanFactory;
import com.qull.springarch.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 21:02
 */
public class AspectJAutoProxyCreator implements BeanPostProcessor {

    ConfigurableBeanFactory beanFactory;

    @Override
    public Object beforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object afterInitialization(Object bean, String beanName) throws BeansException {
        // 如果这个Bean本身就是Advice及其子类，那就不需要再生成动态代理了
        if(isInfrastructureClass(bean.getClass())) {
            return bean;
        }
        List<Advice> advices = getCandidateAdvices(bean);
        if (advices.isEmpty()) {
            return bean;
        }
        return createProxy(advices, bean);
    }

    private Object createProxy(List<Advice> advices, Object bean) {
        AopConfigSupport config = new AopConfigSupport();
        for (Advice advice : advices) {
            config.addAdvice(advice);
        }

        Set<Class> targetInterfaces = ClassUtils.getAllInterfacesForClassAsSet(bean.getClass());
        for (Class targetInterface : targetInterfaces) {
            config.addInterface(targetInterface);
        }

        config.setTargetObject(bean);

        AopProxyFactory proxyFactory = null;
        if (config.getProxiedInterfaces().length == 0) {
            proxyFactory = new CglibProxyFactory(config);
        }else {
            // jdk代理
            //
        }
        return proxyFactory.getProxy();
    }

    private List<Advice> getCandidateAdvices(Object bean) {
        List<Object> advices = this.beanFactory.getBeansByType(Advice.class);
        List<Advice> result = new ArrayList<>();
        for (Object obj : advices) {
            Pointcut pc = ((Advice) obj).getPointcut();
            if(canApply(pc, bean.getClass())) {
                result.add((Advice) obj);
            }
        }
        return result;
    }

    private boolean canApply(Pointcut pc, Class<?> targetClass) {
        MethodMatcher methodMatcher = pc.getMethodMatcher();

        Set<Class> classes = new LinkedHashSet<>(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
        classes.add(targetClass);
        for (Class clazz : classes) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (methodMatcher.matches(method)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass);
    }

    public void setBeanFactory(ConfigurableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
